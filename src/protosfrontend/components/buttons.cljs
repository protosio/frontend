(ns components.buttons
  (:require
    [re-frame.core :as rf]))

(defn submit-button [text dispatch-value style disabled?]
  [:button {:type "button"
            :on-click #(rf/dispatch dispatch-value)
            :class (str "btn btn-" style )
            :disabled (if disabled? true false)}
           text])

(defn submit-button-spinner [text dispatch-value style disabled? loading?]
  [:button {:type "button"
            :on-click #(rf/dispatch dispatch-value)
            :class (str "btn btn-" style)
            :disabled (if disabled? true false)}
           (if loading? [:i {:class "fa fa-spin fa-circle-o-notch"}] text)])
