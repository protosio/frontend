(ns protosfrontend.init.events
    (:require
        [re-frame.core :as rf]
        [protosfrontend.util :as util]))

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
  [{db :db} [_ step-nr]]
  (let [result (condp = step-nr
                2 {:dispatch [:get-dns-providers]}
                3 {:dispatch [:get-cert-providers]}
                4 {:dispatch [:get-init-apps]})
        step-done (get-in db [:init-wizard (keyword (str "step" step-nr)) :done])]
    (if step-done
      {}
      result))))

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
    [{db :db} [_ step installer-id name]]
    (let [installer-params (get-in db [:init-wizard step :form])]
      {:dispatch [:http-post {:url (util/createurl ["e" "apps"])
                              :on-success [:create-app-during-init-success step]
                              :on-failure [:init-failure step]
                              :post-data {:installer-id installer-id :name name :installer-params installer-params}}]})))

(rf/reg-event-fx
  :remove-and-install-provider
  (fn remove-and-install-provider-handler
    [{db :db} [_ step event]]
    (let [_ (get-in db [:init-wizard step :form])
          provider-type (cond (= step :step2) "dns"
                              (= step :step3) "certificate")]
      {:dispatch [:http-delete {:url (util/createurl ["e" "init" (str "provider?provides=" provider-type)])
                                :on-success event
                                :on-failure [:init-failure step]}]
       :db  (-> db
            (assoc-in [:init-wizard step :inprogress] true)
            (assoc-in [:init-wizard step :alert] nil))})))

(rf/reg-event-fx
  :create-app-during-init-success
  (fn create-app-during-init-success-handler
    [{db :db} [_ step result]]
    {:dispatch [:check-init-task step (:id result) result]
     :db (-> db
             (assoc-in [:init-wizard step :task] (keyword (:id result))))}))

(rf/reg-event-fx
  :check-init-task
  (fn check-init-task-handler
    [{db :db} [_ step task-id task]]
    (let [task-unfinished? (util/task-unfinished? task)
          result {:db (assoc-in db [:tasks (keyword task-id)] task)}]
          (if task-unfinished?
            ;; if task is not in final state retrieve the task
            (assoc result :dispatch-debounce {:id (keyword (str "get-init-task-" task-id))
                                              :timeout 1000
                                              :dispatch [:http-get {:url (util/createurl ["e" "tasks" task-id])
                                                                    :on-success [:check-init-task step task-id]
                                                                    :on-failure [:init-failure step]}]})
            ;; if task is in final state set the done flag in the step struct
            (if (= (:status task) "finished")
                (-> result (assoc-in [:db :init-wizard step :done] true)
                           (assoc-in [:db :init-wizard step :inprogress] false)
                           (assoc-in [:db :init-wizard step :alert] {:type "success" :message "Provider installed successfully"}))
                (-> result (assoc-in [:db :init-wizard step :done] false)
                           (assoc-in [:db :init-wizard step :inprogress] false)
                           (assoc-in [:db :init-wizard step :alert] {:type "danger" :message (get-in task [:progress :state])})))))))

;; -- Register user and domain (step1) ---------------------------------------------

(rf/reg-event-fx
  :register-user-domain
  (fn register-user-domain-handler
    [{db :db} _]
    {:dispatch [:http-post {:url (util/createurl ["init" "register"])
                            :on-success [:register-user-domain-success]
                            :on-failure [:init-failure :step1]
                            :post-data (get-in db [:init-wizard :step1 :form])}]
     :db (assoc-in db [:init-wizard :step1 :alert] nil)}))

(rf/reg-event-fx
  :register-user-domain-success
  (fn register-user-domain-success-handler
    [{db :db} [_ result]]
    {:dispatch [:save-auth result]
     :db (-> db
             (assoc-in [:auth] result)
             (assoc-in [:init-wizard :step1 :done] true)
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
             (assoc-in [:init-wizard :step2 :provider-list] result))}))

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
             (assoc-in [:init-wizard :step3 :provider-list] result))}))

;; -- Create DNS and TLS certificate (step4) -----------------------

(rf/reg-event-fx
  :get-init-apps
  (fn get-init-apps-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "apps"])
                           :on-success [:save-response [:apps]]
                           :on-failure [:init-failure :step4]}]}))

(rf/reg-event-fx
  :create-init-resources
  (fn create-init-resources-handler
    [{db :db} _]
    {:dispatch [:http-post {:url (util/createurl ["e" "init" "resources"])
                            :on-success [:retrieve-init-resources]
                            :on-failure [:init-failure :step4]
                            :post-data {}}]
     :db (-> db
             (assoc-in [:init-wizard :step4 :inprogress] 3)
             (assoc-in [:init-wizard :step4 :alert] nil))}))

(rf/reg-event-fx
  :retrieve-init-resources
  (fn retrieve-init-resources-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "init" "resources"])
                           :on-success [:check-init-resources]
                           :on-failure [:init-failure :step4]}]}))

(rf/reg-event-fx
  :check-init-resources
  (fn check-init-resources-handler
    [{db :db} [_ resources]]
    (let [result {:db (assoc-in db [:init-wizard :step4 :resources] resources)}
          resources-created (if (= (count resources) 0)
                                false
                                (every? true? (for [[_ v] resources]
                                                    (= (:status v) "created"))))]
          (if (= resources-created false)
            ;; if resources are not in created state retrieve the resources again
            (assoc result :dispatch-debounce {:id (keyword "get-init-resources")
                                              :timeout 3000
                                              :dispatch [:retrieve-init-resources]})
            ;; if resources are created, finish the init process
            (-> result (assoc-in [:dispatch] [:finish-and-redirect])
                       (assoc-in [:db :init-wizard :step4 :alert] {:type "success" :message "Resources created successfully"}))))))

(rf/reg-event-fx
  :finish-and-redirect
  (fn finish-and-redirect-handler
    [_ _]
    {:dispatch [:http-get {:url (util/createurl ["e" "init" "finish"])
                           :on-success [:save-response [:init-wizard :step4 :dashboard]]
                           :on-failure [:init-failure :step4]}]}))
