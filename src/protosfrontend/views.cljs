(ns protosfrontend.views
    (:require
        [components.header :as header]
        [components.cards :as cards]
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

(defn regular-page [inner title active-page]
  [:div {:class "page-main"}
    [header/top "Alex G" "Admin" active-page]
    [:div.my-3.my-md-5
      [inner title]]])

(defn init-page []
  [:div {:class "page-single"}
    [initviews/init-wizard]])

(defn login-page []
  [:div {:class "page-single"}
    [authviews/login-form]])

(defn dashboard-page [title]
  [:div {:class "page-main"}]
    [:div {:class "container"}
      (if title
        [:div.page-header [:h1.page-title title]])
      [:div {:class "row row-cards"}
        [:div {:class "col-6 col-sm-4 col-lg-2"} [cards/stats "Apps" 3]]
        [:div {:class "col-6 col-sm-4 col-lg-2"} [cards/stats "Resources" 7]]
        [:div {:class "col-6 col-sm-4 col-lg-2"} [cards/stats "Providers" 2]]]])

(defn current-page []
  (let [[active-page & params]  @(rf/subscribe [:active-page])]
    (condp = active-page
      :init-page         [init-page]
      :login-page        [login-page]
      :dashboard-page    [regular-page dashboard-page "Dashboard" active-page]
      :installer-page    [regular-page #(apply installer/installer-page params)]
      :installers-page   [regular-page installer/installers-page "Installers" active-page]
      :app-page          [regular-page #(apply app/app-page params)]
      :apps-page         [regular-page app/apps-page "Apps" active-page]
      :resources-page    [regular-page resource/resources-page "Resources" active-page]
      :resource-page     [regular-page #(apply resource/resource-page params)])))

)
