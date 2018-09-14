(ns protosfrontend.views
    (:require
        [components.header :as header]
        [dashboard.sidebar :as sidebar]
        [dashboard.navbar :as navbar]
        [dashboard.app :as app]
        [dashboard.installer :as installer]
        [dashboard.resource :as resource]
        [init.views :as initviews]
        [auth.views :as authviews]
        [re-frame.core :as rf]
        [free-form.re-frame :as free-form]
        [free-form.bootstrap-3]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "brown")}

;; ---------------------------------------
;; Pages

; (defn regular-page [inner]
;    [:div {:class "flex-fill"}
;       [navbar/menu]

;       [:div {:class "my-3 my-md-5"}
;         [sidebar/sidebar]
;         [:div {:class "main-panel"}
;           [:div {:class "content-wrapper"}
;             [inner]]]]])

(defn regular-page [inner title]
  [:div
    [header/top]

    [:div.my-3.my-md-5
      (if title
        [:div.container [:div.page-header [:h1.page-title title]]])
      [inner]]
  ])

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
    [:div {:class "page-main"}
      (condp = active-page
      :init-page         [init-page]
      :login-page        [login-page]
      :dashboard-page    [regular-page dashboard-page "Dashboard"]
      :installer-page    [regular-page #(apply installer/installer-page params)]
      :installers-page   [regular-page installer/installers-page]
      :app-page          [regular-page #(apply app/app-page params)]
      :apps-page         [regular-page app/apps-page]
      :resources-page    [regular-page resource/resources-page]
      :resource-page     [regular-page #(apply resource/resource-page params)])]))

)
