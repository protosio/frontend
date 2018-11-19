(ns protosfrontend.util
  (:require
    [clojure.string   :as string]
    [re-frame.core    :as rf]
    [re-frame.router  :as router]
    [re-frame.loggers :refer [console]]
    [cljs-time.core   :as tc]
    [cljs-time.format :as tf]))

(defn fmap
  [f m]
  (into (empty m) (for [[k v] m] [k (f v)])))

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
    (tf/unparse (tf/formatters :date-hour-minute-second-fraction) (tf/parse (tf/formatter :basic-date-time) time))
    "n/a"))

(defn time-str [time]
  (if time
    (tf/unparse (tf/formatters :date-hour-minute-second-fraction) time)
    nil))

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

(defn app-creating? [app]
  (if (some #(= (:status app) %) ["creating"])
    true
    false))

(defn apps-creating? [apps]
  (if (> (count (filter app-creating? (vals apps))) 0)
    true
    false))

;;
;; Task helpers
;;

(defn replace-time-in-task [task]
  (let [started-at (tf/parse (tf/formatters :basic-date-time) (:started-at task))
        finished-at (if (:finished-at task)
                        (tf/parse (tf/formatters :basic-date-time) (:finished-at task))
                        nil)]
       (-> task
           (assoc :started-at started-at)
           (assoc :finished-at finished-at))))

(defn sort-tasks [tasks]
  (into (sorted-map-by (fn [id1 id2]
                           (let [task1 (get-in tasks [id1 :started-at])
                                 task2 (get-in tasks [id2 :started-at])]
                                 (cond
                                   (= id1 id2) 0
                                   (nil? task1) 1
                                   (nil? task2) -1
                                   (tc/before? task1 task2) -1
                                   (tc/after? task1 task2) 1
                                   :else 0))))
        tasks))

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
  (fn dispatch-debounce-handler
    [dispatches]
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
