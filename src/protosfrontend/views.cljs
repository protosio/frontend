(ns protosfrontend.views
    (:require
        [re-frame.core :as rf]
        [baking-soda.bootstrap3 :as b3]
        [free-form.re-frame :as free-form]
        [free-form.bootstrap-3]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

;;-------------------------
;; Page components

 (defn menu []
   [:nav {:class "navbar navbar-default navbar-static-top"}
    [:div {:class "container"}
     [:div {:class "navbar-header"}
      [:button {:type "button" :class "navbar-toggle collapsed" :data-toggle "collapse" :data-target "#navbar" :aria-expanded "false" :aria-controls "navbar"}
       [:span {:class "sr-only"} "Toggle navigation"]
       [:span {:class "icon-bar"}]
       [:span {:class "icon-bar"}]
       [:span {:class "icon-bar"}]]
      [:a {:class "navbar-brand" :href "#/"} "Protos"]]
     [:div {:id "navbar" :class "collapse navbar-collapse"}
      [:ul {:class "nav navbar-nav"}
       [:li [:a {:href "#/"} "Home"]]
       [:li [:a {:href "#/installers"} "Installers"]]
       [:li [:a {:href "#/resources"} "Resources"]]
       [:li [:a {:href "#/about"} "About page"]]]]]])

;;-- Modal components -----------------------

 (defn custom-installer-params
  [params]
  (for [fld params]
      [:free-form/field {:type  :text
                        :keys   [:installer-params (keyword fld)]
                        :label fld}]))

 (defn modal-create-app []
   [b3/Modal {:show    @(rf/subscribe [:show-modal])
              :on-hide #(rf/dispatch [:close-modal :create-app-modal])}
    [b3/ModalHeader {:close-button true}
        [b3/ModalTitle
         "Create application"]]
    [b3/ModalBody
     (let [data @(rf/subscribe [:form-data])
           installer-id @(rf/subscribe [:modal-params])]
      [free-form/form data (:-errors data) :update-form-data :bootstrap-3
       (into [:form.form-horizontal {:noValidate true}
        [:free-form/field {:type  :hidden
                           :key   :imageid}]
        [:free-form/field {:type  :text
                           :key   :name
                           :label "Name"}]
        [:free-form/field {:type  :text
                           :key   :command
                           :label "Command"}]
        [:free-form/field {:type  :text
                           :key   :publicports
                           :label "Public ports"}]]
        (custom-installer-params @(rf/subscribe [:installer-params installer-id])))])]

    [b3/ModalFooter
     [b3/Button {:on-click #(rf/dispatch [:close-modal :create-app-modal])} "Close"]
     [b3/Button {:bs-style "primary" :on-click #(rf/dispatch [:create-app])} "Create"]]])

 (defn modal-installer-metadata []
   [b3/Modal {:show    @(rf/subscribe [:show-modal])
              :on-hide #(rf/dispatch [:close-modal :add-metadata-modal])}
    [b3/ModalHeader {:close-button true}
        [b3/ModalTitle
         "Add installer metadata"]]
    (let [data @(rf/subscribe [:form-data])]
     [b3/ModalBody
       [free-form/form data (:-errors data) :update-form-data :bootstrap-3
        [:form.form-vertical {:noValidate true}
         [:free-form/field {:type  :textarea
                            :key   :metadata
                            :label "JSON formated metadata"}]]]])

    (let [installer-id @(rf/subscribe [:modal-params])]
     [b3/ModalFooter
      [b3/Button {:on-click #(rf/dispatch [:close-modal :add-metadata-modal])} "Close"]
      [b3/Button {:bs-style "primary" :on-click #(rf/dispatch [:create-installer-metadata installer-id])} "Add"]])])

 (defn current-modal []
  (let [active-modal  @(rf/subscribe [:active-modal])]
    [:div (condp = active-modal
           :create-app-modal    [modal-create-app]
           :add-metadata-modal  [modal-installer-metadata]
           [:div])]))

;;-- List components -----------------------

 (defn installer-list []
   [:table {:class "table table-hover"}
    [:tbody
     [:tr
       [:th "Name"]
       [:th "ID"]]
     (let [installers @(rf/subscribe [:installers])]
       (for [{Name :name, ID :id} (vals installers)]
         [:tr {:key ID :style {:width "100%"}}
           [:td [:a {:href (str "/#/installers/" ID)} Name]]
           [:td ID]]))]])

 (defn app-list []
   [:table {:class "table table-hover"}
    [:tbody
     [:tr
       [:th "Name"]
       [:th "ID"]
       [:th "Status"]]
     (let [apps @(rf/subscribe [:apps])]
       (for [{name :name, id :id, status :status} (vals apps)]
         [:tr {:key id :style {:width "100%"}}
           [:td [:a {:href (str "/#/apps/" id)} name]]
           [:td id]
           [:td status]]))]])

 (defn resource-list []
   [:table {:class "table table-hover"}
    [:tbody
     [:tr
       [:th "ID"]
       [:th "Status"]
       [:th "Type"]
       [:th "App"]]
     (let [resources @(rf/subscribe [:resources])]
       (for [{type :type, id :id, status :status, app-id :app} (vals resources)]
         [:tr {:key id :style {:width "100%"}}
           [:td [:a {:href (str "/#/resources/" id)} id]]
           [:td status]
           [:td type]
           [:td [:a {:href (str "/#/apps/" app-id)} app-id]]]))]])



;; ---------------------------------------
;; Pages

 (defn regular-page [left right]
   [:div
     (menu)
     [:div {:class "container"}
      [:div {:class "row"}
       [:div {:class "col-md-1"}
        left]
       [:div {:class "col-md-11"}
        right]]]])

 (defn apps-page
   []
   [:div
    (regular-page
     [:button {:on-click #(rf/dispatch [:get-apps])} "Refresh"]
     [app-list])])

 (defn app-page
   [id]
   [:div
     (menu)
     [:div {:class "container"}
      (let [apps @(rf/subscribe [:apps])
            app (get apps (keyword id))
            app-id (:id app)]
        [:div
         [:h1 (:name app)]
         [:div.app-details
          [:div.row
           [:div.col-md-12
             [:div.table-responsive
               [:table {:class "table table-striped table-bordered"}
                [:tbody
                 [:tr
                   [:th "ID"]
                   [:td (:id app)]]
                 [:tr
                   [:th "Name"]
                   [:td (:name app)]]
                 [:tr
                   [:th "Image ID"]
                   [:td (:imageid app)]]
                 [:tr
                  [:th "Command"]
                  [:td (:command app)]]
                 [:tr
                  [:th "Status"]
                  [:td (:status app)]]]]]]]]
         [b3/Button {:bs-style "danger"
                     :on-click #(rf/dispatch [:remove-app app-id])} "Remove"]
         [b3/Button {:bs-style "primary"
                     :on-click #(rf/dispatch [:app-state app-id "stop"])} "Stop"]
         [b3/Button {:bs-style "success"
                     :on-click #(rf/dispatch [:app-state app-id "start"])} "Start"]
         [b3/Button {:bs-style "primary"
                     :on-click #(rf/dispatch [:get-app app-id])} "Refresh"]])]])

 (defn installers-page
   []
   [:div
    (regular-page
     [:button {:on-click #(rf/dispatch [:get-installers])} "Refresh"]
     [installer-list])])

 (defn installer-page
   [id]
   [:div
     (menu)
     [:div {:class "container"}
      (let [installer @(rf/subscribe [:installer id])]
        [:div
         [:h1 (:Name installer)]
         [:div.installer-details
          [:div.row
           [:div.col-md-12
             [:div.table-responsive
               [:table {:class "table table-striped table-bordered"}
                [:tbody
                 [:tr
                   [:th "ID"]
                   [:td (:id installer)]]
                 [:tr
                  [:th "Name"]
                  [:td (:name installer)]]
                (if (get-in installer [:metadata])
                 nil
                 [:tr
                  [:th "Metadata"]
                  [:td "Not present"]])
                 [:tr
                  [:th "Description"]
                  [:td (get-in installer [:metadata :description])]]
                 [:tr
                  [:th "Provides"]
                  [:td (clojure.string/join  " " (get-in installer [:metadata :provides]))]]]]]]]]
         [b3/Button {:bs-style "danger"
                     :on-click #(rf/dispatch [:remove-resource :installers (:id installer) [:installers-page]])} "Remove"]
         [b3/Button {:bs-style "primary"
                     :on-click #(rf/dispatch [:open-modal :create-app-modal (:id installer)])} "Create app"]
         [b3/Button {:bs-style "primary"
                     :on-click #(rf/dispatch [:open-modal :add-metadata-modal (:id installer)])} "Add metadata"]
         [:div
          (current-modal)]])]])

 (defn resources-page
   []
   [:div
    (regular-page
     [:button {:on-click #(rf/dispatch [:get-resources])} "Refresh"]
     [resource-list])])

(defn resource-page
  [id]
  [:div
    (menu)
    [:div {:class "container"}
      (let [resources @(rf/subscribe [:resources])
            resource (get resources (keyword id))
            resource-id (:id resources)]
        [:div
          [:h1 (:id resource)]
          [:div.resource-details
            [:div.row
            [:div.col-md-12
              [:div.table-responsive
                [:table {:class "table table-striped table-bordered"}
                  [:tbody
                  [:tr
                    [:th "Type"]
                    [:td (:type resource)]]
                  [:tr
                    [:th "App"]
                    [:td (:app resource)]]
                  [:tr
                    [:th "Status"]
                    [:td (:status resource)]]]]]]]]])]])


 (defn about-page []
   [:div
    (menu)
    [:div {:class "container"}
     [:h1 "This is the about text"]]])

 (defn current-page []
  (let [[active-page & params]  @(rf/subscribe [:active-page])]
    [:div (condp = active-page
           :installer-page    [#(apply installer-page params)]
           :installers-page   [installers-page]
           :app-page          [#(apply app-page params)]
           :apps-page         [apps-page]
           :resources-page    [resources-page]
           :resource-page     [#(apply resource-page params)]
           :about-page        [about-page])]))

)
