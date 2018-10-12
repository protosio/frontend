(ns init.events
    (:require
        [re-frame.core :as rf]
        [protosfrontend.events :as util]
        [clairvoyant.core :refer-macros [trace-forms]]
        [re-frame-tracer.core :refer [tracer]]))

(trace-forms {:tracer (tracer :color "green")}

(rf/reg-event-fx
  :increment-init-step
  (fn increment-init-step-handler
    [{db :db} _]
    (let  [init-step (get-in db [:init-wizard :step])
           new-init-step (+ init-step 1)
           final-step (if (= :init-step 4)
                           4
                           new-init-step)]
          {:dispatch [:step-action final-step]
           :db (assoc-in db [:init-wizard :step] final-step)})))

(rf/reg-event-fx
  :decrement-init-step
  (fn decrement-init-step-handler
    [{db :db} _]
    (let  [init-step (get-in db [:init-wizard :step])
           new-init-step (- init-step 1)
           final-step (if (= :init-step 1)
                           1
                           new-init-step)]
          {:dispatch [:step-action final-step]
           :db (assoc-in db [:init-wizard :step] final-step)})))

(rf/reg-event-fx
  :step-action
  (fn step-action-handler
  [{db :db} [_ step]]
  (let [event (condp = step
                2         [:get-dns-providers]
                3         [:get-cert-providers]
                [:noop])
        installer-downloaded (get-in db [:init-wizard step :downloaded])]
    (if installer-downloaded
      {}
      {:dispatch event}))))

(rf/reg-event-fx
  :init-failure
  (fn init-failure-handler
    [{db :db} [_ step result]]
    {:db (assoc-in db [:init-wizard step :alert] {:type "danger" :message (get-in result [:response :error])})}))


;; -- Common operations step 2 & 3 ----------------------------------------

(rf/reg-event-fx
  :get-installer-init
  (fn get-installer-init-handler
    [_ [_ step id]]
    {:dispatch [:http-get {:url (util/createurl ["e" "installers" (name id)])
                           :on-success [:get-installer-init-success step]
                           :on-failure [:init-failure step]}]}))

(rf/reg-event-fx
  :get-installer-init-success
  (fn get-installer-init-success-handler
    [{db :db} [_ step result]]
    (let [version (last (sort (keys (:versions result))))]
    {:db (-> db
             (assoc-in [:init-wizard step :installer-run-params] (get-in result [:versions version :params]))
             (assoc-in [:init-wizard step :downloaded] true))})))

(rf/reg-event-fx
  :create-app-during-init
  (fn create-app-during-init-handler
    [{db :db} [_ step]]
    (let [installer-id (get-in db [:init-wizard step :selected-provider])
          installer-params (get-in db [:init-wizard step :form])
          installers (get-in db [:init-wizard step :provider-list])
          name (:name (installer-id installers))
          version (last (sort (:versions (get installers installer-id))))]
    {:dispatch [:http-post {:url (util/createurl ["e" "apps"])
                            :on-success [:create-app-during-init-success step]
                            :on-failure [:init-failure step]
                            :post-data {:installer-id installer-id :installer-version version :name name :installer-params installer-params}}]})))

(rf/reg-event-fx
  :create-app-during-init-success
  (fn create-app-during-init-success-handler
    [{db :db} [_ step result]]
    {:dispatch [:start-app-during-init step (:id result)]
     :db (-> db
             (assoc-in [:init-wizard step :app] result)
             (assoc-in [:init-wizard step :alert] {:type "success" :message "Provider created successfully"}))}))

(rf/reg-event-fx
  :start-app-during-init
  (fn start-app-during-init-handler
    [{db :db} [_ step app-id]]
    {:dispatch [:http-post {:url (util/createurl ["e" "apps" app-id "action"])
                            :on-success [:start-app-during-init-success step]
                            :on-failure [:init-failure step]
                            :post-data {:name "start"}}]}))

(rf/reg-event-fx
  :start-app-during-init-success
  (fn start-app-during-init-success-handler
    [{db :db} [_ step _]]
    {:db (assoc-in db [:init-wizard step :alert] {:type "success" :message "Provider started successfully"})}))

;; -- Register user and domain (step1) ---------------------------------------------

(rf/reg-event-fx
  :register-user-domain
  (fn register-user-domain-handler
    [{db :db} _]
    {:dispatch [:http-post {:url (util/createurl ["auth" "register"])
                            :on-success [:register-user-domain-success]
                            :on-failure [:init-failure :step1]
                            :post-data (get-in db [:init-wizard :step1 :form])}]}))

(rf/reg-event-fx
  :register-user-domain-success
  (fn register-user-domain-success-handler
    [{db :db} [_ result]]
    {:dispatch [:save-auth result]
     :db (-> db
             (assoc-in [:auth] result)
             (assoc-in [:init-wizard :step1 :alert] {:type "success" :message "User and domain registered successfully"}))}))

;; -- Download and run dns provider (step2) ----------------------------------------

(rf/reg-event-fx
  :get-dns-providers
  (fn get-dns-providers-handler
    [_ _]
    {:dispatch [:http-get {:url (str (util/createurl ["e" "store" "search"]) "?provides=dns")
                           :on-success [:get-dns-providers-success]
                           :on-failure [:init-failure :step2]}]}))

(rf/reg-event-fx
  :get-dns-providers-success
  (fn get-dns-providers-success-handler
    [{db :db} [_ result]]
    {:db (-> db
             (assoc-in [:init-wizard :step2 :provider-list] result)
             (assoc-in [:init-wizard :step2 :alert] {:type "success" :message "DNS providers retrieved successfully"}))}))

(rf/reg-event-fx
  :download-dns-provider
  (fn download-dns-provider-handler
    [{db :db} [_ installer-name]]
    (let [id (get-in db [:init-wizard :step2 :selected-provider])
          installers (get-in db [:init-wizard :step2 :provider-list])
          name (:name (id installers))
          version (last (sort (:versions (get installers id))))]
    {:dispatch [:http-post {:url (util/createurl ["e" "store" "download"])
                            :on-success [:download-dns-provider-success id]
                            :on-failure [:init-failure :step2]
                            :post-data {:id id :version version :name name}}]})))

(rf/reg-event-fx
  :download-dns-provider-success
  (fn download-dns-provider-success-handler
    [{db :db} [_ installer-id result]]
    {:dispatch [:get-installer-init :step2 installer-id]
     :db (assoc-in db [:init-wizard :step2 :alert] {:type "success" :message "DNS provider downloaded successfully"})}))


;; -- Download and run certificate provider (step3) -----------------------

(rf/reg-event-fx
  :get-cert-providers
  (fn get-cert-providers-handler
    [_ _]
    {:dispatch [:http-get {:url (str (util/createurl ["e" "store" "search"]) "?provides=certificate")
                           :on-success [:get-cert-providers-success]
                           :on-failure [:init-failure :step3]}]}))

(rf/reg-event-fx
  :get-cert-providers-success
  (fn get-cert-providers-success-handler
    [{db :db} [_ result]]
    {:db (-> db
             (assoc-in [:init-wizard :step3 :provider-list] result)
             (assoc-in [:init-wizard :step3 :alert] {:type "success" :message "SSL certificate providers retrieved successfully"}))}))

(rf/reg-event-fx
  :download-cert-provider
  (fn download-cert-provider-handler
    [{db :db} [_ installer-name]]
    (let [id (get-in db [:init-wizard :step3 :selected-provider])
          installers (get-in db [:init-wizard :step3 :provider-list])
          name (:name (id installers))
          version (last (sort (:versions (get installers id))))]
    {:dispatch [:http-post {:url (util/createurl ["e" "store" "download"])
                            :on-success [:download-cert-provider-success id]
                            :on-failure [:init-failure :step3]
                            :post-data {:id id :version version :name name}}]})))

(rf/reg-event-fx
  :download-cert-provider-success
  (fn download-cert-provider-success-handler
    [{db :db} [_ installer-id result]]
    {:dispatch [:get-installer-init :step3 installer-id]
     :db (assoc-in db [:init-wizard :step3 :alert] {:type "success" :message "SSL certificate provider downloaded successfully"})}))

;; -- Create DNS and TLS certificate (step4) -----------------------

(rf/reg-event-fx
  :create-init-resources
  (fn create-init-resources-handler
    [_ _]
    {:dispatch [:http-post {:url (util/createurl ["e" "protos" "resources"])
                            :on-success [:create-init-resources-success]
                            :on-failure [:init-failure :step4]
                            :post-data {}}]}))

(rf/reg-event-fx
  :create-init-resources-success
  (fn create-init-resources-success-handler
    [{db :db} [_ result]]
    {:dispatch-n (vec (for [id (keys result)]
                           [:start-timer id 5000 [:retrieve-init-resource id]]))
     :db (-> db
             (assoc-in [:init-wizard :step4 :alert] {:type "success" :message "Successfully requested resource creation"})
             (assoc-in [:init-wizard :step4 :resources] result))}))

(rf/reg-event-fx
  :retrieve-init-resource
  (fn retrieve-init-resource-handler
    [_ [_ id]]
    {:dispatch [:http-get {:url (util/createurl ["e" "resources" (name id)])
                           :on-success [:retrieve-init-resource-success id]
                           :on-failure [:init-failure :step4]}]}))

(rf/reg-event-fx
  :retrieve-init-resource-success
  (fn retrieve-init-resource-success-handler
    [{db :db} [_ id result]]
    {:dispatch (if (= "created" (:status result))
               [:stop-timer id]
               [:noop])
     :db (assoc-in db [:init-wizard :step4 :resources id] result)}))

)
