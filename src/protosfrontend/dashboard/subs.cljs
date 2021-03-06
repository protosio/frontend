(ns protosfrontend.dashboard.subs
    (:require
     [re-frame.core       :as rf]
     [protosfrontend.util :as util]
     [linked.core         :as linked]))

(rf/reg-sub
  :services
  (fn services-sub
    [db _]
    (-> db
        :services)))

(rf/reg-sub
  :hwstats
  (fn hwstats-sub
    [db _]
    (-> db
        :hwstats)))

(rf/reg-sub
  :apps
  (fn apps-sub
    [db _]
    (-> db
        :apps)))

(rf/reg-sub
  :app
  (fn app-sub
    [db [_ id]]
    (-> db
        :apps
        id)))

(rf/reg-sub
  :installers
  (fn installers-sub
    [db _]
    (-> db
        :installers)))

(rf/reg-sub
  :installer
  (fn installer-sub
    [db [_ installer-id]]
    (get-in db [:installers (keyword installer-id)])))

(rf/reg-sub
  :store-installers
  (fn store-installers-sub
    [db _]
    (-> db
        :store
        :installers)))

(rf/reg-sub
  :store-installer
  (fn store-installers-sub
    [db [_ installer-id]]
    (get-in db [:store :installers (keyword installer-id)])))

(rf/reg-sub
  :store-filter-form
  (fn store-filter-form-sub
    [db _]
    (get-in db [:store :filter])))

(rf/reg-sub
  :installer-params
  (fn installer-params-sub
    [db [_ installer-id]]
    (get-in db [:installers (keyword installer-id) :metadata :params])))

(rf/reg-sub
  :tasks
  (fn tasks-sub
    [db _]
    (rseq (:tasks db))))

(rf/reg-sub
  :tasks-filter
  (fn tasks-filter-sub
    [db [_ ids]]
    (rseq (into (linked/map)
                (util/sort-tasks (select-keys (:tasks db)
                                              (mapv keyword ids)))))))

(rf/reg-sub
  :task
  (fn task-sub
    [db [_ id]]
    (get-in db [:tasks id])))

(rf/reg-sub
  :resources
  (fn resources-sub
    [db _]
    (:resources db)))

(rf/reg-sub
  :resources-filter
  (fn resources-filter-sub
    [db [_ ids]]
    (select-keys (:resources db)
                 (mapv keyword ids))))

(rf/reg-sub
  :resource
  (fn resource-sub
    [db [_ id]]
    (-> db
        :resources
        id)))

(rf/reg-sub
  :create-app-form
  (fn create-app-form-sub
    [db _]
    (get-in db [:create-app :form])))

(rf/reg-sub
  :alert-dashboard
  (fn alert-dashboard-sub
    [db _]
    (get-in db [:dashboard :alert])))
