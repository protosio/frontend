(ns protosfrontend.views
    (:require
        [viewcomponents.sidebar :as sidebar]
        [viewcomponents.navbar :as navbar]
        [re-frame.core :as rf]
        [baking-soda.core :as b]
        [free-form.re-frame :as free-form]
        [free-form.bootstrap-3]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

;;-------------------------
;; Page components

;;-- Modal components -----------------------

(defn modal-login []
  [b/Modal {:is-open  @(rf/subscribe [:show-modal])
            :toggle   #(rf/dispatch [:close-modal :login-modal])}
   [b/ModalHeader {:close-button true} "Login"]
   [b/ModalBody
    (let [data @(rf/subscribe [:form-data])]
     [free-form/form data (:-errors data) :update-form-data :bootstrap-3
      (into [:form.form-horizontal {:noValidate true}
       [:free-form/field {:type  :text
                          :key   :username
                          :label "Username"}]
       [:free-form/field {:type  :text
                          :key   :password
                          :label "Password"}]])])]

   [b/ModalFooter
    [b/Button {:on-click #(rf/dispatch [:close-modal :login-modal])} "Close"]
    [b/Button {:bs-style "primary" :on-click #(rf/dispatch [:login])} "Login"]]])

 (defn custom-installer-params
  [params]
  (for [fld params]
      [:free-form/field {:type  :text
                        :keys   [:installer-params (keyword fld)]
                        :label fld}]))

 (defn modal-create-app []
   [b/Modal {:is-open  @(rf/subscribe [:show-modal])
             :toggle   #(rf/dispatch [:close-modal :create-app-modal])}
    [b/ModalHeader {:close-button true} "Create application"]
    [b/ModalBody
     (let [data @(rf/subscribe [:form-data])
           installer-id @(rf/subscribe [:modal-params])]
      [free-form/form data (:-errors data) :update-form-data :bootstrap-3
       (into [:form.form-horizontal {:noValidate true}
        [:free-form/field {:type  :hidden
                           :key   :installer-id}]
        [:free-form/field {:type  :text
                           :key   :name
                           :label "Name"}]]
        (custom-installer-params @(rf/subscribe [:installer-params installer-id])))])]

    [b/ModalFooter
     [b/Button {:on-click #(rf/dispatch [:close-modal :create-app-modal])} "Close"]
     [b/Button {:bs-style "primary" :on-click #(rf/dispatch [:create-app])} "Create"]]])

 (defn modal-installer-metadata []
   [b/Modal {:is-open @(rf/subscribe [:show-modal])
             :toggle  #(rf/dispatch [:close-modal :add-metadata-modal])}
    [b/ModalHeader {:close-button true} "Add installer metadata"]
    (let [data @(rf/subscribe [:form-data])]
     [b/ModalBody
       [free-form/form data (:-errors data) :update-form-data :bootstrap-3
        [:form.form-vertical {:noValidate true}
         [:free-form/field {:type  :textarea
                            :key   :metadata
                            :label "JSON formated metadata"}]]]])

    (let [installer-id @(rf/subscribe [:modal-params])]
     [b/ModalFooter
      [b/Button {:on-click #(rf/dispatch [:close-modal :add-metadata-modal])} "Close"]
      [b/Button {:bs-style "primary" :on-click #(rf/dispatch [:create-installer-metadata installer-id])} "Add"]])])

 (defn current-modal []
  (let [active-modal  @(rf/subscribe [:active-modal])]
    [:div (condp = active-modal
           :create-app-modal    [modal-create-app]
           :add-metadata-modal  [modal-installer-metadata]
           :login-modal         [modal-login]
           [:div])]))

;;-- List components -----------------------

(defn installer-list []
  [:div {:class "col-lg-12 grid-margin stretch-card"}
    [:div {:class "card"}
      [:div {:class "card-body"}
        [:h4 {:class "card-title"} "Installers"]
        [:div {:class "table-responsive"}
          [:table {:class "table table-striped"}
            [:thead
              [:tr
                [:th "Name"]
                [:th "ID"]]]
            [:tbody
            (let [installers @(rf/subscribe [:installers])]
              (for [{Name :name, ID :id} (vals installers)]
                [:tr {:key ID :style {:width "100%"}}
                  [:td [:a {:href (str "/#/installers/" ID)} Name]]
                  [:td ID]]))]]]]]])

(defn app-list []
  [:div {:class "col-lg-12 grid-margin stretch-card"}
    [:div {:class "card"}
      [:div {:class "card-body"}
        [:h4 {:class "card-title"} "Apps"]
        [:div {:class "table-responsive"}
          [:table {:class "table table-striped"}
            [:thead
              [:tr
                [:th "Name"]
                [:th "ID"]
                [:th "Status"]]]
            [:tbody
            (let [apps @(rf/subscribe [:apps])]
              (for [{name :name, id :id, status :status} (vals apps)]
                [:tr {:key id :style {:width "100%"}}
                  [:td [:a {:href (str "/#/apps/" id)} name]]
                  [:td id]
                  [:td status]]))]]]]]])

(defn resource-list []
  [:div {:class "col-lg-12 grid-margin stretch-card"}
    [:div {:class "card"}
      [:div {:class "card-body"}
        [:h4 {:class "card-title"} "Resources"]
        [:div {:class "table-responsive"}
          [:table {:class "table table-striped"}
            [:thead
              [:tr
                [:th "ID"]
                [:th "Status"]
                [:th "Type"]
                [:th "App"]]]
            [:tbody
          (let [resources @(rf/subscribe [:resources])]
            (for [{type :type, id :id, status :status, app-id :app} (vals resources)]
              [:tr {:key id :style {:width "100%"}}
                [:td [:a {:href (str "/#/resources/" id)} id]]
                [:td status]
                [:td type]
                [:td [:a {:href (str "/#/apps/" app-id)} app-id]]]))]]]]]])

;; ---------------------------------------
;; Pages

(defn regular-page [left right]
   [:div
      [navbar/menu]
      [:div {:class "container-fluid page-body-wrapper"}
        [sidebar/sidebar]
        [:div {:class "main-panel"}
          [:div {:class "content-wrapper"}
            right]]]])

(defn dashboard-page []
  [:div
    [regular-page]])

(defn apps-page []
   [:div
    [regular-page
     [:button {:on-click #(rf/dispatch [:get-apps])} "Refresh"]
     [:div {:class "row"}
      [app-list]]]])

(defn app-page
   [id]
   [:div
     (navbar/menu)
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
                   [:th "Installer ID"]
                   [:td (:installer-id app)]]
                 [:tr
                  [:th "Status"]
                  [:td (:status app)]]]]]]]]
         [b/Button {:bs-style "danger"
                     :on-click #(rf/dispatch [:remove-app app-id])} "Remove"]
         [b/Button {:bs-style "primary"
                     :on-click #(rf/dispatch [:app-state app-id "stop"])} "Stop"]
         [b/Button {:bs-style "success"
                     :on-click #(rf/dispatch [:app-state app-id "start"])} "Start"]
         [b/Button {:bs-style "primary"
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
     (navbar/menu)
     [:div {:class "container"}
      (let [installer @(rf/subscribe [:installer id])
            metadata (get-in installer [:metadata])]
        [:div
         [:h1 (:Name installer)]
         [b/Button {:bs-style "danger" :on-click #(rf/dispatch [:remove-installer id])} "Remove"]
         [b/Button {:bs-style "primary" :on-click #(rf/dispatch [:open-modal :create-app-modal (:id installer)])} "Create app"]
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
                (if metadata
                 nil
                 [:tr
                  [:th "Metadata"]
                  [:td "Not present"]])
                 [:tr
                  [:th "Description"]
                  [:td (-> metadata :description)]]
                 [:tr
                  [:th "Provides"]
                  [:td (clojure.string/join  " " (-> metadata :provides))]]]]]]]]

         ])]])

 (defn resources-page
   []
   [:div
    (regular-page
     [:button {:on-click #(rf/dispatch [:get-resources])} "Refresh"]
     [resource-list])])

(defn resource-page
  [id]
  [:div
    (navbar/menu)
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

 (defn current-page []
  (let [[active-page & params]  @(rf/subscribe [:active-page])]
    [:div
      [current-modal]
      [:div (condp = active-page
            :dashboard-page    [dashboard-page]
            :installer-page    [#(apply installer-page params)]
            :installers-page   [installers-page]
            :app-page          [#(apply app-page params)]
            :apps-page         [apps-page]
            :resources-page    [resources-page]
            :resource-page     [#(apply resource-page params)])]]))

)
