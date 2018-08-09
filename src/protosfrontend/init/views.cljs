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

(defn submit-button [text dispatch-value disabled? loading?]
    [:button {:type "button"
              :on-click #(rf/dispatch [dispatch-value])
              :class (str "btn btn-rounded btn-outline-primary submit-btn mr-2" (when disabled? " disabled"))}
              text (when loading? [:i {:class "fa fa-spin fa-spinner"}])])

(defn navigation-buttons [button-text event disabled? loading?]
  [:div {:class "form-group"}
    [:div {:class "form-row text-center"}
      [:div {:class "col-12"}
        [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary mr-2" :on-click #(rf/dispatch [:decrement-init-step])} [:i {:class "mdi mdi-arrow-left"}]]
        [submit-button button-text event disabled? loading?]
        [:button {:type "button" :class "btn btn-icons btn-rounded btn-outline-primary" :on-click #(rf/dispatch [:increment-init-step])} [:i {:class "mdi mdi-arrow-right"}]]]]])

(defn input-field [properties]
  ^{:key (:id properties)} [:div {:class "form-group"}
    [:div {:class "input-group"}
      [:input properties]
      [:div {:class "input-group-append"}
        [:span {:class "input-group-text"}
          [:i {:class "mdi mdi-check-circle-outline"}]]]]])

(defn single-select-list [properties items]
  [:div {:class "form-group"}
      [:ul.list-group properties
      (for [item items]
        [:div.list-group-item {:key (:id item)} (:name item)])]])

(defn step1 []
  [:div {:class "col-lg-4 mx-auto"}
    [:h2 {:class "text-center mb-4"} "User & domain"]
    [:div {:class "auto-form-wrapper"}
      [bind-fields
        [:div
          [input-field {:field :text :id :init-wizard.step1.form.username :class "form-control" :placeholder "Username"}]
          [input-field {:field :text :id :init-wizard.step1.form.name :class "form-control" :placeholder "Name"}]
          [input-field {:field :password :id :init-wizard.step1.form.password :class "form-control" :placeholder "Password"}]
          [input-field {:field :password :id :init-wizard.step1.form.confirmpassword :class "form-control" :placeholder "Confirm password"}]
          [input-field {:field :text :id :init-wizard.step1.form.domain :class "form-control" :placeholder "Domain"}]]
        (form-events [:init-form-step1])]
      (let [loading? @(rf/subscribe [:loading?])]
          [navigation-buttons "Register" :register-user-domain loading? loading?])
      [alert :alert-init1]]])

(defn step2 []
  [:div {:class "col-lg-4 mx-auto"}
    [:h2 {:class "text-center mb-4"} "Domain provider"]
    [:div {:class "auto-form-wrapper"}
      [:h5 {:class "mb-4"} "Select a DNS provider"]
      (let [dns-providers @(rf/subscribe [:dns-providers])]
        (if-not (empty? dns-providers)
          [bind-fields
            (single-select-list {:field :single-select :id :init-wizard.step2.selected-dns-provider} dns-providers)
            (form-events [:init-form-step2])]))
      (let [dns-params @(rf/subscribe [:dns-provider-params])]
        (if-not (empty? dns-params)
          [:div
          [:div {:class "border-top my-3"}]
          [bind-fields
            (into [:div ] (for [field dns-params]
                               [input-field {:field :text :id (keyword (str "init-wizard.step2.form." field)) :class "form-control" :placeholder field}]))
            (form-events [:init-form-step2])]]))
      (let [loading? @(rf/subscribe [:loading?])
           disabled? (or (not @(rf/subscribe [:selected-dns-provider])) loading?)
           dns-params @(rf/subscribe [:dns-provider-params])]
        (if (empty? dns-params)
          [navigation-buttons "Download" :download-dns-provider disabled? loading?]
          [navigation-buttons "Run" :run-dns-provider disabled? loading?]))
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
