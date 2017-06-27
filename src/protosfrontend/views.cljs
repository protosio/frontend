(ns protosfrontend.views
    (:require
        [re-frame.core :as rf]
        [baking-soda.bootstrap3 :as b3]
        [free-form.re-frame :as free-form]
        [free-form.bootstrap-3]))

;;-------------------------
;; Page elements

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
      [:li [:a {:href "#/about"} "About page"]]]]]])

(defn modal-create-app []
  [b3/Modal {:show    @(rf/subscribe [:show-create-app-modal])
             :on-hide #(rf/dispatch [:show-create-app-modal false])}
   [b3/ModalHeader {:close-button true}
       [b3/ModalTitle
        "Create application"]]
   [b3/ModalBody
    (let [data @(rf/subscribe [:create-app-form])]
     [free-form/form data (:-errors data) :create-app-form :bootstrap-3
      [:form.form-horizontal {:noValidate true}
       [:free-form/field {:type  :text
                          :key   :name
                          :label "Name"}]
       [:free-form/field {:type  :text
                          :key   :command
                          :label "Command"}]]])]

   [b3/ModalFooter
    [b3/Button {:on-click #(rf/dispatch [:show-create-app-modal false])} "Close"]
    [b3/Button {:bs-style "primary" :on-click #(rf/dispatch [:create-resource :apps :create-app-form [:apps-page]])} "Create"]]])

(defn modal-installer-metadata []
  [b3/Modal {:show    @(rf/subscribe [:show-installer-metadata-modal])
             :on-hide #(rf/dispatch [:show-installer-metadata-modal false])}
   [b3/ModalHeader {:close-button true}
       [b3/ModalTitle
        "Add installer metadata"]]
   [b3/ModalBody
    (let [data @(rf/subscribe [:add-metadata-form])]
     [free-form/form data (:-errors data) :add-metadata-form :bootstrap-3
      [:form.form-vertical {:noValidate true}
       [:free-form/field {:type  :text
                          :key   :metadata
                          :label "JSON formated metadata"}]]])]

   [b3/ModalFooter
    [b3/Button {:on-click #(rf/dispatch [:show-installer-metadata-modal false])} "Close"]
    [b3/Button {:bs-style "primary" :on-click #(rf/dispatch [:create-resource :installer :add-metadata-form [:apps-page]])} "Add"]]])


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


(defn regular-page [left right]
  [:div
    (menu)
    [:div {:class "container"}
     [:div {:class "row"}
      [:div {:class "col-md-1"}
       left]
      [:div {:class "col-md-11"}
       right]]]])

;; -------------------------
;; Pages
(defn apps-page
  []
  [:div
   (regular-page
    [:button {:on-click #(rf/dispatch [:update-list :apps])} "Refresh"]
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
                    :on-click #(rf/dispatch [:remove-resource :apps app-id [:apps-page]])} "Remove"]
        [b3/Button {:bs-style "primary"
                    :on-click #(rf/dispatch [:create-resource-action :apps app-id "stop"])} "Stop"]
        [b3/Button {:bs-style "success"
                    :on-click #(rf/dispatch [:create-resource-action :apps app-id "start"])} "Start"]
        [b3/Button {:bs-style "primary"
                    :on-click #(rf/dispatch [:update-resource :apps app-id])} "Refresh"]
        [:div
         (modal-create-app)]])]])

(defn installers-page
  []
  [:div
   (regular-page
    [:button {:on-click #(rf/dispatch [:update-list :installers])} "Refresh"]
    [installer-list])])

(defn installer-page
  [id]
  [:div
    (menu)
    [:div {:class "container"}
     (let [installers @(rf/subscribe [:installers])
           installer (get installers (keyword id))]
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
                [:tr
                 [:th "Description"]
                 [:td (get-in installer [:metadata :description])]]]]]]]]
        [b3/Button {:bs-style "danger"
                    :on-click #(rf/dispatch [:remove-resource :installers (:ID installer) [:installers-page]])} "Remove"]
        [b3/Button {:bs-style "primary"
                    :on-click #(rf/dispatch [:show-create-app-modal true (:ID installer)])} "Create app"]
        [b3/Button {:bs-style "primary"
                    :on-click #(rf/dispatch [:show-installer-metadata-modal true (:ID installer)])} "Add metadata"]
        [:div
         (modal-create-app)]
        [:div
         (modal-installer-metadata)]])]])

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
          :about-page        [about-page])]))