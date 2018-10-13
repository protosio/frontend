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
    [:div {:class "my-3 my-md-5"}
      [inner title]]])

(defn init-page []
  [:div {:class "page-single"}
    [initviews/init-wizard]])

(defn login-page []
  [:div {:class "page-single"}
    [authviews/login-form]])

(defn current-page []
  (let [[active-page _ item-id]  @(rf/subscribe [:active-page])]
    (condp = active-page
      :init-page            [init-page]
      :login-page           [login-page]
      :dashboard-page       [regular-page home/home-page "Dashboard" active-page]
      :task-page            [regular-page task/task-page item-id active-page]
      :tasks-page           [regular-page task/tasks-page "Tasks" active-page]
      :app-page             [regular-page app/app-page item-id active-page]
      :apps-page            [regular-page app/apps-page "Apps" active-page]
      :store-page           [regular-page store/store-page "Store" active-page]
      :store-installer-page [regular-page store/store-installer-page item-id active-page]
      :resources-page       [regular-page resource/resources-page "Resources" active-page]
      :resource-page        [regular-page resource/resource-page item-id active-page])))

)
