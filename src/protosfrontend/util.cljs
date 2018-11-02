(ns protosfrontend.util
  (:require
    [clojure.string :as string]
    [re-frame.core      :as rf]
    [re-frame.router    :as router]
    [re-frame.loggers   :refer [console]]
    [cljs-time.format   :as timeformat]))

(defn createurl [urlkeys]
    (str "/api/v1/" (string/join "/" (flatten [urlkeys]))))

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
   "failed"            "red"
   "stopped"           "blue"
   "creating"          "yellow"
   "unknown"           "gray"
   "blue"))

(defn task-status-color [status]
  (condp = status
   "requested"  "secondary"
   "inprogress" "warning"
   "failed"     "danger"
   "finished"   "success"
   "warning"))

(defn resource-status-color [status]
  (condp = status
   "requested" "warning"
   "unknown"   "secondary"
   "created"   "success"
   "warning"))

(defn shorten-time [time]
  (if time
    (timeformat/unparse (timeformat/formatters :date-hour-minute-second-fraction) (timeformat/parse (timeformat/formatter "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'") time))
    "n/a"))

(defn trunc [s n]
  (str (subs s 0 (min (count s) n)) "..."))

(defn task-unfinished? [task]
  (if (some #(= (:status task) %) ["finished" "failed"])
    false
    true))

(defn tasks-unfinished? [tasks]
  (if (> (count (filter task-unfinished? (vals tasks))) 0)
    true
    false))

;;
;; Event debouncer
;;

(def debounced-events (atom {}))

(defn cancel-timeout [id]
  (js/clearTimeout (:timeout (@debounced-events id)))
  (swap! debounced-events dissoc id))

(defn cancel-all-timeouts []
  (doseq [[id val] @debounced-events]
    (if (not (:no-cancel val))
      (cancel-timeout id))))

(rf/reg-fx
  :dispatch-debounce
  (fn [dispatches]
    (let [dispatches (if (sequential? dispatches) dispatches [dispatches])]
      (doseq [{:keys [id action dispatch timeout no-cancel]
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
                              :dispatch dispatch
                              :no-cancel no-cancel}))
          :cancel (cancel-timeout id)
          :cancel-all (cancel-all-timeouts)
          :flush (let [ev (get-in @debounced-events [id :dispatch])]
                   (cancel-timeout id)
                   (router/dispatch ev))
          (console :warn "re-frame: ignoring bad :dispatch-debounce action:" action "id:" id))))))
