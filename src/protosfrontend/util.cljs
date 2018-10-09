(ns protosfrontend.util
  (:require
    [re-frame.core :as rf]
    [cljs-time.format :as timeformat]))

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
  (timeformat/unparse (timeformat/formatters :date-hour-minute-second-fraction) (timeformat/parse (timeformat/formatter "yyyy-MM-dd'T'HH:mm:ss'.'SSS'Z'") time)))

(defn trunc [s n]
  (str (subs s 0 (min (count s) n)) "..."))
