(ns auth.views
    (:require
        [re-frame.core :as rf]
        [reagent-forms.core :refer [bind-fields]]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

(defn form-events
  [dbpath]
  {:get (fn [path] @(rf/subscribe [:form-field-value path]))
   :save! (fn [path value] (rf/dispatch [:set-form-value path value]))
   :update! (fn [path save-fn value]
              (rf/dispatch [:set-form-value path
                              (save-fn @(rf/subscribe [:form-field-value path]) value)]))
   :doc (fn [] @(rf/subscribe dbpath))})

(defn alert
  [alert-sub]
  (let [alert-data @(rf/subscribe alert-sub)]
    (when alert-data
      [:div {:class "form-group"}
        [:div {:class "form-row text-center"}
          [:div {:class "col-12"}
            [:div {:class (str "alert alert-" (:type alert-data)) :role "alert"} (:message alert-data)]]]])))

(defn submit-button [text dispatch-value disabled? loading?]
    [:button {:type "button"
              :on-click #(rf/dispatch dispatch-value)
              :class (str "btn btn-rounded btn-outline-primary submit-btn mr-2" (when disabled? " disabled"))}
              text (when loading? [:i {:class "fa fa-spin fa-spinner"}])])

(defn input-field [properties]
  ^{:key (:id properties)} [:div {:class "form-group"}
    [:div {:class "input-group"}
      [:input properties]
      [:div {:class "input-group-append"}
        [:span {:class "input-group-text"}
          [:i {:class "mdi mdi-check-circle-outline"}]]]]])

(defn login-form []
    [:div {:class "container-scroller"}
        [:div {:class "container-fluid page-body-wrapper full-page-wrapper auth-page"}
        [:div {:class "content-wrapper d-flex align-items-center auth theme-one"}
            [:div {:class "row w-100"}
            [:div {:class "col-lg-4 mx-auto"}
            [:div {:class "auto-form-wrapper"}
                [bind-fields
                    [:div
                      [input-field {:field :text :id :login.form.username :class "form-control" :placeholder "Username"}]
                      [input-field {:field :password :id :login.form.password :class "form-control" :placeholder "Password"}]]
                    (form-events [:init-form :step1])]
              (let [loading? @(rf/subscribe [:loading?])]
                [:div {:class "form-group"}
                  [:div {:class "form-row text-center"}
                    [:div {:class "col-12"}
                    [submit-button "Login" [:login] loading? loading?]]]])
                [alert [:alert-login]]]]]]]])

)