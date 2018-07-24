(ns init.views
  (:require
    [re-frame.core :as rf]
    [reagent-forms.core :refer [bind-fields]]))

(def form-events
  {:get (fn [path] @(rf/subscribe [:form-field-value path]))
   :save! (fn [path value] (rf/dispatch [:set-form-value path value]))
   :update! (fn [path save-fn value]
              (rf/dispatch [:set-form-value path
                              (save-fn @(rf/subscribe [:form-field-value path]) value)]))
   :doc (fn [] @(rf/subscribe [:form-data]))})

(defn alert []
  (let [alert-data @(rf/subscribe [:alert])]
    (when alert-data
      [:div {:class "form-group"}
        [:div {:class "form-row text-center"}
          [:div {:class "col-12"}
            [:div {:class (str "alert alert-" (:type alert-data)) :role "alert"} (:message alert-data)]]]])))

(defn submit-button [text dispatch-value]
  (let [loading? @(rf/subscribe [:loading?])]
    [:button {:type "button"
              :on-click #(rf/dispatch dispatch-value)
              :class (str "btn btn-rounded btn-outline-primary submit-btn mr-2" (when loading? " disabled"))}
              text (when loading? [:i {:class "fa fa-spin fa-spinner"}])]))

(defn navigation-buttons []
  [:div {:class "form-group"}
    [:div {:class "form-row text-center"}
      [:div {:class "col-12"}
        [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary mr-2" :on-click #(rf/dispatch [:decrement-init-step])} [:i {:class "mdi mdi-arrow-left"}]]
        [submit-button "Register" [:register-user-domain]]
        [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary" :on-click #(rf/dispatch [:increment-init-step])} [:i {:class "mdi mdi-arrow-right"}]]]]])

(defn input-field [properties]
  [:div {:class "form-group"}
    [:div {:class "input-group"}
      [:input properties]
      [:div {:class "input-group-append"}
        [:span {:class "input-group-text"}
          [:i {:class "mdi mdi-check-circle-outline"}]]]]])

(defn step1 []
  [:div {:class "col-lg-4 mx-auto"}
    [:h2 {:class "text-center mb-4"} "User & domain"]
    [:div {:class "auto-form-wrapper"}
      [bind-fields
        [:div
          [input-field {:field :text :id :username :class "form-control" :placeholder "Username"}]
          [input-field {:field :password :id :password :class "form-control" :placeholder "Password"}]
          [input-field {:field :password :id :confirmpassword :class "form-control" :placeholder "Confirm password"}]
          [input-field {:field :text :id :domain :class "form-control" :placeholder "Domain"}]]
        form-events]
      [navigation-buttons]
      [alert]]])

(defn step2 []
  [:div {:class "col-lg-4 mx-auto"}
    [:h2 {:class "text-center mb-4"} "Domain provider"]
    [:div {:class "auto-form-wrapper"}
      [bind-fields
        [:div
          [input-field {:field :text :id :username :class "form-control" :placeholder "Username"}]
          [input-field {:field :text :id :domain :class "form-control" :placeholder "Domain"}]]
        form-events]
      [navigation-buttons]]])

(defn init-wizard []
  [:div {:class "container-scroller"}
    [:div {:class "container-fluid page-body-wrapper full-page-wrapper auth-page"}
      [:div {:class "content-wrapper d-flex align-items-center auth theme-one"}
        [:div {:class "row w-100"}
        (let [init-step  @(rf/subscribe [:init-step])]
            (condp = init-step
            1         [step1]
            2         [step2]
            3         [step2]
            4         [step2]
            ))]]]])
