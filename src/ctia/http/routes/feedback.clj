(ns ctia.http.routes.feedback
  (:require [compojure.api.sweet :refer :all]
            [ctia.flows.crud :as flows]
            [ctia.http.routes.common :refer [paginated-ok PagingParams]]
            [ctia.schemas
             [common :as c]
             [feedback :refer [NewFeedback realize-feedback StoredFeedback]]]
            [ctia.store :refer :all]
            [ring.util.http-response :refer :all]
            [schema-tools.core :as st]
            [schema.core :as s]))

(s/defschema FeedbackQueryParams
  (st/merge
   PagingParams
   {:entity_id s/Str
    (s/optional-key :sort_by) (s/enum :id :feedback :reason)}))

(defroutes feedback-routes
  (context "/feedback" []
    :tags ["Feedback"]
    (POST "/" []
      :return StoredFeedback
      :body [feedback NewFeedback {:description "a new Feedback on an entity"}]
      :summary "Adds a new Feedback"
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :create-feedback
      :login login
      (ok (flows/create-flow :realize-fn realize-feedback
                             :store-fn #(create-feedback @feedback-store %)
                             :entity-type :feedback
                             :login login
                             :entity feedback)))
    (GET "/" []
      :return [StoredFeedback]
      :query [params FeedbackQueryParams]
      :summary "Search Feedback"
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :read-feedback

      (paginated-ok
       (list-feedback @feedback-store
                      (select-keys params [:entity_id])
                      (dissoc params :entity_id))))
    (GET "/:id" []
      :return (s/maybe StoredFeedback)
      :summary "Gets a Feedback by ID"
      :path-params [id :- s/Str]
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :read-feedback
      (if-let [d (read-feedback @feedback-store id)]
        (ok d)
        (not-found)))
    (DELETE "/:id" []
      :no-doc true
      :path-params [id :- s/Str]
      :summary "Deletes a feedback"
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :delete-feedback
      :login login
      (if (flows/delete-flow :get-fn #(read-feedback @feedback-store %)
                             :delete-fn #(delete-feedback @feedback-store %)
                             :entity-type :feedback
                             :id id
                             :login login)
        (no-content)
        (not-found))))
  (context "/feedbacks" []
    :tags ["Feedback"]
    (POST "/" []
      :return [c/ID]
      :body [feedbacks [NewFeedback] {:description "a list of new Feedback"}]
      :header-params [api_key :- (s/maybe s/Str)]
      :summary "Adds a list of new Feedback"
      :capabilities :create-feedback
      :login login
      (ok (map (fn [feedback]
                 (-> (flows/create-flow :entity-type :feedback
                                        :realize-fn realize-feedback
                                        :store-fn #(create-feedback @feedback-store %)
                                        :login login
                                        :entity feedback)
                     :id))
               feedbacks)))))
