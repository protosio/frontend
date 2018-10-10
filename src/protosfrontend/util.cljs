(ns protosfrontend.util
  (:require
    [re-frame.core      :as rf]
    [re-frame.registrar :as reg]
    [re-frame.router    :as router]
    [re-frame.loggers   :refer [console]]
    [cljs-time.format   :as timeformat]))

(defn form-events
  [dbpath]
  {:get (fn [path] @(rf/subscribe [:form-field-value path]))
   :save! (fn [path value] (rf/dispatch [:set-form-value path value]))
   :update! (fn [path save-fn value]
      (rf/dispatch [:set-form-value path
                    (save-fn @(rf/subscribe [:form-field-value path]) value)]))
   :doc (fn [] @(rf/subscribe dbpath))})

(defn app-status-color [status]
  (condp = status
   "running"           "green"
   "missing container" "red"
   "stopped"           "red"
   "blue"))

(defn task-status-color [status]
  (condp = status
   "requested"  "secondary"
   "inprogress" "warning"
   "failed"     "danger"
   "finished"   "success"
   "warning"))

(defn shorten-time [time]
  (if time
    (timeformat/unparse (timeformat/formatters :date-hour-minute-second-fraction) (timeformat/parse (timeformat/formatter "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'") time))
    "n/a"))

(defn trunc [s n]
  (str (subs s 0 (min (count s) n)) "..."))
;;
;; Event debouncer
;;

(def debounced-events (atom {}))

(defn cancel-timeout [id]
  (js/clearTimeout (:timeout (@debounced-events id)))
  (swap! debounced-events dissoc id))

(reg/register-handler :fx
  :dispatch-debounce
  (fn [dispatches]
    (let [dispatches (if (sequential? dispatches) dispatches [dispatches])]
      (doseq [{:keys [id action dispatch timeout]
               :or   {action :dispatch}}
              dispatches]
        (case action
          :dispatch (do
                      (cancel-timeout id)
                      (swap! debounced-events assoc id
                             {:timeout  (js/setTimeout (fn []
                                                        (swap! debounced-events dissoc id)
                                                        (router/dispatch dispatch))
                                                      timeout)
                              :dispatch dispatch}))
          :cancel (cancel-timeout id)
          :flush (let [ev (get-in @debounced-events [id :dispatch])]
                   (cancel-timeout id)
                   (router/dispatch ev))
          (console :warn "re-frame: ignoring bad :dispatch-debounce action:" action "id:" id))))))