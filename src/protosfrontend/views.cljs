(ns protosfrontend.views
    (:require
        [components.header :as header]
        [components.cards :as cards]
        [dashboard.home :as home]
        [dashboard.app :as app]
        [dashboard.store :as store]
        [dashboard.task :as task]
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

(defn current-page []
  (let [[active-page item-id & params]  @(rf/subscribe [:active-page])]
    (condp = active-page
      :init-page            [init-page]
      :login-page           [login-page]
      :dashboard-page       [regular-page home/home-page "Dashboard" active-page]
      :task-page            [regular-page #(apply task/task-page params)]
      :tasks-page           [regular-page task/tasks-page "Tasks" active-page]
      :app-page             [regular-page #(apply app/app-page params)]
      :apps-page            [regular-page app/apps-page "Apps" active-page]
      :store-page           [regular-page store/store-page "Store" active-page]
      :store-installer-page [regular-page #(apply store/store-installer-page params)]
      :resources-page       [regular-page resource/resources-page "Resources" active-page]
      :resource-page        [regular-page #(apply resource/resource-page params)])))

)
