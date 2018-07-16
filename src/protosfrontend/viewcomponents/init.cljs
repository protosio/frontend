(ns viewcomponents.init
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

(defn navigation-buttons []
  [:div {:class "form-row text-center"}
        [:div {:class "col-12"}
          [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary mr-2"} [:i {:class "mdi mdi-arrow-left"}]]
          [:button {:type "button" :class "btn btn-rounded btn-outline-primary submit-btn mr-2"} "Register"]
          [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary"} [:i {:class "mdi mdi-arrow-right"}]]]])

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
      [navigation-buttons]]])

(defn init-wizard []
[:div {:class "container-scroller"}
  [:div {:class "container-fluid page-body-wrapper full-page-wrapper auth-page"}
    [:div {:class "content-wrapper d-flex align-items-center auth theme-one"}
      [:div {:class "row w-100"}
        [step1]]]]])