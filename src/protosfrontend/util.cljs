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

(defn submit-button [text dispatch-value style disabled?]
  [:button {:type "button"
            :on-click #(rf/dispatch dispatch-value)
            :class (str "btn btn-outline-" style " submit-btn mr-2" (when disabled? " disabled"))}
            text])

(defn submit-button-spinner [text dispatch-value style disabled? loading?]
  [:button {:type "button"
            :on-click #(rf/dispatch dispatch-value)
            :class (str "btn btn-outline-" style " submit-btn mr-2" (when disabled? " disabled"))}
            text (when loading? [:i {:class "fa fa-spin fa-spinner"}])])

(defn app-status-color [status]
  (condp = status
   "running"           "green"
   "missing container" "red"
   "stopped"           "red"
   "blue"))
