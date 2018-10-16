(ns init.views
  (:require
    [re-frame.core :as rf]
    [protosfrontend.util :as util]
    [components.buttons :as buttons]
    [components.alerts :as alerts]
    [reagent-forms.core :refer [bind-fields]]
    [clairvoyant.core :refer-macros [trace-forms]]
    [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

(defn card-header []
  [:div {:class "card-header"}
    [:div {:class "mx-auto p-1"}
      [:img {:src "/static/images/protos-logo.svg" :class "h-7" :alt "protos logo"}]]])

(defn navigation-buttons [button-text event disabled? loading? step step-done]
  [:div {:class "form-row"}
    [:div {:class "mx-auto"}
      [:div {:class "col"}
        [:button {:type "button" :class "btn btn-sm btn-icons btn-rounded btn-outline-primary mr-2" :disabled (if (= step :step1) true false) :on-click #(rf/dispatch [:decrement-init-step])} [:i {:class "fe fe-arrow-left"}]]
        [buttons/submit-button-spinner button-text event "primary btn-sm mr-2" disabled? loading?]
        [:button {:type "button" :class "btn btn-sm btn-icons btn-rounded btn-outline-primary" :disabled (if (or (= step :step4) (not step-done)) true false) :on-click #(rf/dispatch [:increment-init-step])} [:i {:class "fe fe-arrow-right"}]]]]])

(defn input-field [properties]
  ^{:key (:id properties)} [:div {:class "form-group"}
    [:input properties]])

(defn single-select-list [properties items]
  [:div {:class "form-group"}
      [:ul (merge {:class "list-group"} properties)
      (for [[id item] items]
        [:li.list-group-item {:key id} (:name item)])]])

(defn task-progress
  [task]
  [:div {:class "task-progress"}
    [:div {:class "clearfix"}
      [:div {:class "float-left"}
        [:strong (str (get-in task [:progress :percentage]) "%")]]
      [:div {:class "float-right"}
        [:span {:class (str "status-icon bg-" (util/task-status-color (:status task)))}] (str " " (:status task))]]
    [:div {:class "progress progress-xs"}
      [:div {:class "progress-bar bg-green"
             :aria-valuemax "100"
             :aria-valuemin "0"
             :aria-valuenow (get-in task [:progress :percentage])
             :style {:width (str (get-in task [:progress :percentage]) "%")}
             :role "progressbar"}]]])

(defn step1 []
  [:form {:class "card align-middle"}
    [card-header]
    [alerts/for-card [:alert-init :step1]]
    [:div {:class "card-body p-5"}
      [:h4 {:class "text-center mb-4"} "User & domain"]
      [:div {:class "auto-form-wrapper"}
        [bind-fields
          [:div
            [input-field {:field :text :id :init-wizard.step1.form.username :class "form-control" :placeholder "username"}]
            [input-field {:field :text :id :init-wizard.step1.form.name :class "form-control" :placeholder "name"}]
            [input-field {:field :password :id :init-wizard.step1.form.password :class "form-control" :placeholder "password"}]
            [input-field {:field :password :id :init-wizard.step1.form.confirmpassword :class "form-control" :placeholder "confirm password"}]
            [input-field {:field :text :id :init-wizard.step1.form.domain :class "form-control" :placeholder "domain"}]]
          (util/form-events [:init-form :step1])]]]
    [:div {:class "card-footer p-3"}
    (let [loading? @(rf/subscribe [:loading?])
          step-done @(rf/subscribe [:init-step-done :step1])]
          [navigation-buttons "Register" [:register-user-domain] loading? loading? :step1 step-done])]])

(defn step2 []
  (let [providers @(rf/subscribe [:providers :step2])
        selected-provider @(rf/subscribe [:selected-provider :step2])
        selected-version (get-in providers [selected-provider :version])
        provider-name (get-in providers [selected-provider :name])
        params (get-in providers [selected-provider :provider-params])
        task @(rf/subscribe [:init-step-task :step2])
        loading? @(rf/subscribe [:loading?])
        disabled? (or (not selected-provider) loading?)
        step-done @(rf/subscribe [:init-step-done :step2])]
  [:form {:class "card align-middle"}
    [card-header]
    [alerts/for-card [:alert-init :step2]]
    [:div {:class "card-body p-5"}
      [:h4 {:class "text-center mb-4"} "Install a DNS provider"]
        [:div {:class "auto-form-wrapper"}
          ;; when we have the providers show them in a single select list
          (if-not (empty? providers)
            [bind-fields
              (single-select-list {:field :single-select :id :init-wizard.step2.selected-provider} providers)
              (util/form-events [:init-form :step2])])
          ;; if the selected provider requires input parameters display them in a form
          (if-not (empty? params)
            [:div
              [:div {:class "border-top my-3"}]
              [bind-fields
                (into [:div ] (for [field params]
                                  [input-field {:field :text :id (keyword (str "init-wizard.step2.form." field)) :class "form-control" :placeholder field}]))
                (util/form-events [:init-form :step2])]])
          (if-not (empty? task)
            [task-progress task])]]
    [:div {:class "card-footer p-3"}
      [navigation-buttons "Install" [:create-app-during-init :step2 selected-provider provider-name selected-version] disabled? loading? :step2 step-done]]]))

(defn step3 []
  (let [providers @(rf/subscribe [:providers :step3])
        selected-provider @(rf/subscribe [:selected-provider :step3])
        selected-version (get-in providers [selected-provider :version])
        provider-name (get-in providers [selected-provider :name])
        params (get-in providers [selected-provider :provider-params])
        task @(rf/subscribe [:init-step-task :step3])
        loading? @(rf/subscribe [:loading?])
        disabled? (or (not selected-provider) loading?)
        step-done @(rf/subscribe [:init-step-done :step3])]
  [:form {:class "card align-middle"}
    [card-header]
    [alerts/for-card [:alert-init :step3]]
    [:div {:class "card-body p-5"}
      [:h4 {:class "text-center mb-4"} "Install a certificate provider"]
        [:div {:class "auto-form-wrapper"}
          ;; when we have the providers show them in a single select list
          (if-not (empty? providers)
            [bind-fields
              (single-select-list {:field :single-select :id :init-wizard.step3.selected-provider} providers)
              (util/form-events [:init-form :step3])])
          ;; if the selected provider requires input parameters display them in a form
          (if-not (empty? params)
            [:div
              [:div {:class "border-top my-3"}]
              [bind-fields
                (into [:div ] (for [field params]
                                  [input-field {:field :text :id (keyword (str "init-wizard.step3.form." field)) :class "form-control" :placeholder field}]))
                (util/form-events [:init-form :step3])]])
          (if-not (empty? task)
            [task-progress task])]]
    [:div {:class "card-footer p-3"}
      [navigation-buttons "Install" [:create-app-during-init :step3 selected-provider provider-name selected-version] disabled? loading? :step3 step-done]]])) 

(defn step4 []
  (let [resources @(rf/subscribe [:init-resources])
        loading? @(rf/subscribe [:loading?])
        resources-created (if (= (count resources) 0)
                              false
                              (every? true? (for [[k v] resources]
                                                 (= (:status v) "created"))))]
  [:form {:class "card align-middle"}
    [card-header]
    [alerts/for-card [:alert-init :step4]]
    [:div {:class "card-body p-5"}
      [:h4 {:class "text-center mb-4"} "Protos DNS and TLS resources"]
      [:div {:class "auto-form-wrapper"}
        [:h5 {:class "mb-4"} "Resource status"]
        [:ul {:class "list-arrow"}
          (for [[id rsc] resources]
            [:li {:key id} (str (:type rsc) " has status: " (:status rsc))])]]]
    [:div {:class "card-footer p-3"}
    (if resources-created
      [navigation-buttons "Finish" [:restart-and-redirect] loading? loading?]
      [navigation-buttons "Create resurces" [:create-init-resources] loading? loading?])]]))

(defn init-wizard []
  [:div {:class "container"}
    [:div {:class "row"}
      [:div {:class "col col-login mx-auto"}
            (let [init-step  @(rf/subscribe [:init-step])]
                (condp = init-step
                1         [step1]
                2         [step2]
                3         [step3]
                4         [step4]
                ))]]])

)
