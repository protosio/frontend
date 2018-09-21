(ns protosfrontend.util
  (:require
    [re-frame.core :as rf]))

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

(defn trunc [s n]
  (str (subs s 0 (min (count s) n)) "..."))
