(ns ctia.http.routes.actor
  (:require [compojure.api.sweet :refer :all]
            [ctia.flows.crud :as flows]
            [ctia.schemas
             [actor :refer [NewActor realize-actor StoredActor]]
             [common :as c]]
            [ctia.store :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(defroutes actor-routes
  (context "/actor" []
    :tags ["Actor"]
    (POST "/" []
      :return StoredActor
      :body [actor NewActor {:description "a new Actor"}]
      :header-params [api_key :- (s/maybe s/Str)]
      :summary "Adds a new Actor"
      :capabilities :create-actor
      :login login
      (ok (flows/create-flow :entity-type :actor
                             :realize-fn realize-actor
                             :store-fn #(create-actor @actor-store %)
                             :login login
                             :entity actor)))
    (PUT "/:id" []
      :return StoredActor
      :body [actor NewActor {:description "an updated Actor"}]
      :header-params [api_key :- (s/maybe s/Str)]
      :summary "Updates an Actor"
      :path-params [id :- s/Str]
      :capabilities :create-actor
      :login login
      (ok (flows/update-flow :entity-type :actor
                             :get-fn #(read-actor @actor-store %)
                             :realize-fn realize-actor
                             :update-fn #(update-actor @actor-store (:id %) %)
                             :id id
                             :login login
                             :entity actor)))
    (GET "/:id" []
      :return (s/maybe StoredActor)
      :summary "Gets an Actor by ID"
      :path-params [id :- s/Str]
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :read-actor
      (if-let [d (read-actor @actor-store id)]
        (ok d)
        (not-found)))
    (DELETE "/:id" []
      :no-doc true
      :path-params [id :- s/Str]
      :summary "Deletes an Actor"
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :delete-actor
      :login login
      (if (flows/delete-flow :entity-type :actor
                             :get-fn #(read-actor @actor-store %)
                             :delete-fn #(delete-actor @actor-store %)
                             :id id
                             :login login)
        (no-content)
        (not-found))))
  (context "/actors" []
    :tags ["Actor"]
    (POST "/" []
      :return [c/ID]
      :body [actors [NewActor] {:description "a list of new Actor"}]
      :header-params [api_key :- (s/maybe s/Str)]
      :summary "Adds a list of new Actor"
      :capabilities :create-actor
      :login login
      (ok (map (fn [actor]
                 (-> (flows/create-flow :entity-type :actor
                                        :realize-fn realize-actor
                                        :store-fn #(create-actor @actor-store %)
                                        :login login
                                        :entity actor)
                     :id))
               actors)))))
