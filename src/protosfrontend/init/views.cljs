(ns init.views
  (:require
    [re-frame.core :as rf]
    [protosfrontend.util :as util]
    [components.buttons :as buttons]
    [components.alerts :as alerts]
    [components.header :as header]
    [reagent-forms.core :refer [bind-fields]]
    [clairvoyant.core :refer-macros [trace-forms]]
    [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

(defn get-resource-description
  [rsc]
  (condp = (:type rsc)
    "dns" (if (= (get-in rsc [:value :type]) "A")
                "DNS record for dashboard"
                "DNS record for email")
    "certificate" "TLS certificate for dashboard"
    (:type rsc)))

(defn card-header
  []
  [:div {:class "card-header"}
    [:div {:class "card-title"}
      [:img {:src "/static/images/protos-logo.svg" :class "h-7" :alt "protos logo"}]]
    [:div {:class "card-options"}
      [header/loading-indicator]]])

(defn navigation-buttons
  [button-text event disabled? step step-done]
  [:div {:class "form-row"}
    [:div {:class "mx-auto"}
      [:div {:class "col"}
        [:button {:type "button" :class "btn btn-sm btn-icons btn-rounded btn-outline-primary mr-2" :disabled (if (= step :step1) true false) :on-click #(rf/dispatch [:decrement-init-step])} [:i {:class "fe fe-arrow-left"}]]
        [buttons/submit-button button-text event "primary btn-sm mr-2" disabled?]
        [:button {:type "button" :class "btn btn-sm btn-icons btn-rounded btn-outline-primary" :disabled (if (or (= step :step4) (not step-done)) true false) :on-click #(rf/dispatch [:increment-init-step])} [:i {:class "fe fe-arrow-right"}]]]]])

(defn input-field
  [properties]
  ^{:key (:id properties)} [:div {:class "form-group"}
    [:input properties]])

(defn single-select-list
  [properties items]
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

(defn step1
  []
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
          [navigation-buttons "Register" [:register-user-domain] loading? :step1 step-done])]])

(defn step2
  []
  (let [providers @(rf/subscribe [:providers :step2])
        selected-provider @(rf/subscribe [:selected-provider :step2])
        selected-version (get-in providers [selected-provider :version])
        provider-name (get-in providers [selected-provider :name])
        params (get-in providers [selected-provider :provider-params])
        task @(rf/subscribe [:init-step-task :step2])
        inprogress? @(rf/subscribe [:init-step-inprogress :step2])
        disabled? (or (not selected-provider) inprogress?)
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
      [navigation-buttons "Install" [:remove-and-install-provider :step2 [:create-app-during-init :step2 selected-provider provider-name selected-version]] disabled? :step2 step-done]]]))

(defn step3
  []
  (let [providers @(rf/subscribe [:providers :step3])
        selected-provider @(rf/subscribe [:selected-provider :step3])
        selected-version (get-in providers [selected-provider :version])
        provider-name (get-in providers [selected-provider :name])
        params (get-in providers [selected-provider :provider-params])
        task @(rf/subscribe [:init-step-task :step3])
        inprogress? @(rf/subscribe [:init-step-inprogress :step3])
        disabled? (or (not selected-provider) inprogress?)
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
      [navigation-buttons "Install" [:remove-and-install-provider :step3 [:create-app-during-init :step3 selected-provider provider-name selected-version]] disabled? :step3 step-done]]]))

(defn step4
  []
  (let [resources @(rf/subscribe [:init-resources])
        apps @(rf/subscribe [:apps])
        inprogress? @(rf/subscribe [:init-step-inprogress :step4])
        providers-ready (if (= (count apps) 0)
                               false
                               (every? true? (for [[k v] apps]
                                                  (= (:status v) "running"))))
        dashboard-domain @(rf/subscribe [:init-dashboard-domain])
        disabled? (or (not providers-ready) inprogress?)]
    [:form {:class "card align-middle"}
      [card-header]
      [alerts/for-card [:alert-init :step4]]
      [:div {:class "card-body p-5"}
        [:h4 {:class "text-center mb-4"} "Create DNS and TLS resources"]
        [:div {:class "auto-form-wrapper"}
          ;; providers list
          [:ul {:class "list-unstyled"}
            (for [[id app] apps]
              [:li {:key id}
                [:span {:class (str "status-icon bg-" (util/app-status-color (:status app)))}] (str " " (:name app))])]
          [:div {:class "border-top my-3"}]
          ;; resources list
          (if (empty? resources)
            (if providers-ready
              [:p {:class "text-success"} "Ready to create DNS and TLS resources"]
              [:p {:class "text-danger"} "Can't create the required resources until all providers are ready"])
            [:div
              [:ul {:class "list-unstyled"}
                (for [[id rsc] resources]
                  [:li {:key id}
                    [:span {:class (str "status-icon bg-" (util/resource-status-color (:status rsc)))}] (get-resource-description rsc)])]
              (if dashboard-domain
                [:p {:class "text-success"} (str "Initialization complete. Please visit https://" dashboard-domain " to start using your Protos instance")]
                [:p "Waiting for resources to be created..."])])]]
      [:div {:class "card-footer p-3"}
        [navigation-buttons "Create resources" [:create-init-resources] disabled?]]]))

(defn init-wizard
  []
  [:div {:class "container"}
    [:div {:class "row"}
      [:div {:class "col col-login mx-auto"}
            (let [init-step  @(rf/subscribe [:init-step])]
                (condp = init-step
                1         [step1]
                2         [step2]
                3         [step3]
                4         [step4]))]]])

)
