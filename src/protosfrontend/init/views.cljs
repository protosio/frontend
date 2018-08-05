(ns init.views
  (:require
    [re-frame.core :as rf]
    [reagent-forms.core :refer [bind-fields]]))

(defn form-events
  [dbpath]
  {:get (fn [path] @(rf/subscribe [:form-field-value path]))
   :save! (fn [path value] (rf/dispatch [:set-form-value path value]))
   :update! (fn [path save-fn value]
              (rf/dispatch [:set-form-value path
                              (save-fn @(rf/subscribe [:form-field-value path]) value)]))
   :doc (fn [] @(rf/subscribe dbpath))})

(defn alert [alert-sub]
  (let [alert-data @(rf/subscribe [alert-sub])]
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

(defn navigation-buttons [button-text event]
  [:div {:class "form-group"}
    [:div {:class "form-row text-center"}
      [:div {:class "col-12"}
        [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary mr-2" :on-click #(rf/dispatch [:decrement-init-step])} [:i {:class "mdi mdi-arrow-left"}]]
        [submit-button button-text [event]]
        [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary" :on-click #(rf/dispatch [:increment-init-step])} [:i {:class "mdi mdi-arrow-right"}]]]]])

(defn input-field [properties]
  [:div {:class "form-group"}
    [:div {:class "input-group"}
      [:input properties]
      [:div {:class "input-group-append"}
        [:span {:class "input-group-text"}
          [:i {:class "mdi mdi-check-circle-outline"}]]]]])

(defn single-select-list [properties]
  [:div {:class "form-group"}
      [:ul.list-group properties
        [:div.list-group-item {:key :foo} "foo"]
        [:div.list-group-item {:key :bar} "bar"]
        [:div.list-group-item {:key :baz} "baz"]]])

(defn step1 []
  [:div {:class "col-lg-4 mx-auto"}
    [:h2 {:class "text-center mb-4"} "User & domain"]
    [:div {:class "auto-form-wrapper"}
      [bind-fields
        [:div
          [input-field {:field :text :id :init-wizard.step1.username :class "form-control" :placeholder "Username"}]
          [input-field {:field :text :id :init-wizard.step1.name :class "form-control" :placeholder "Name"}]
          [input-field {:field :password :id :init-wizard.step1.password :class "form-control" :placeholder "Password"}]
          [input-field {:field :password :id :init-wizard.step1.confirmpassword :class "form-control" :placeholder "Confirm password"}]
          [input-field {:field :text :id :init-wizard.step1.domain :class "form-control" :placeholder "Domain"}]]
        (form-events [:init-form-step1])]
      [navigation-buttons "Register" :register-user-domain]
      [alert :alert-init1]]])

(defn step2 []
  [:div {:class "col-lg-4 mx-auto"}
    [:h2 {:class "text-center mb-4"} "Domain provider"]
    [:div {:class "auto-form-wrapper"}
      [bind-fields
        (single-select-list {:field :single-select :id :list-selection})
        (form-events [:init-form-step2])]
      [navigation-buttons "Install" :install-dns-provider]
      [alert :alert-init2]]])

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
