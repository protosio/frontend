(ns protosfrontend.views
    (:require
        [viewcomponents.sidebar :as sidebar]
        [viewcomponents.navbar :as navbar]
        [viewcomponents.app :as app]
        [viewcomponents.installer :as installer]
        [viewcomponents.resource :as resource]
        [init.views :as initviews]
        [auth.views :as authviews]
        [re-frame.core :as rf]
        [baking-soda.core :as b]
        [free-form.re-frame :as free-form]
        [free-form.bootstrap-3]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

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

;; ---------------------------------------
;; Pages

(defn regular-page [inner]
   [:div {:class "regular-page"}
      [navbar/menu]
      [:div {:class "container-fluid page-body-wrapper"}
        [sidebar/sidebar]
        [:div {:class "main-panel"}
          [:div {:class "content-wrapper"}
            [inner]]]]])

(defn init-page []
  [:div {:class "init-page"}
    [initviews/init-wizard]])

(defn login-page []
  [:div {:class "login-page"}
    [authviews/login-form]])

(defn dashboard-page []
  [:div "Dashboard placeholder"])


(defn current-page []
  (let [[active-page & params]  @(rf/subscribe [:active-page])]
    [:div {:class "webui"}
      [current-modal]
      (condp = active-page
      :init-page         [init-page]
      :login-page        [login-page]
      :dashboard-page    [regular-page dashboard-page]
      :installer-page    [regular-page #(apply installer/installer-page params)]
      :installers-page   [regular-page installer/installers-page]
      :app-page          [regular-page #(apply app/app-page params)]
      :apps-page         [regular-page app/apps-page]
      :resources-page    [regular-page resource/resources-page]
      :resource-page     [regular-page #(apply resource/resource-page params)])]))

)
