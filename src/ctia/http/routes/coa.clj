(ns ctia.http.routes.coa
  (:require [compojure.api.sweet :refer :all]
            [ctia.flows.crud :as flows]
            [ctia.schemas
             [coa :refer [NewCOA realize-coa StoredCOA]]
             [common :as c]]
            [ctia.store :refer :all]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(defroutes coa-routes
  (context "/coa" []
    :tags ["COA"]
    (POST "/" []
      :return StoredCOA
      :body [coa NewCOA {:description "a new COA"}]
      :summary "Adds a new COA"
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :create-coa
      :login login
      (ok (flows/create-flow :realize-fn realize-coa
                             :store-fn #(create-coa @coa-store %)
                             :entity-type :coa
                             :login login
                             :entity coa)))
    (PUT "/:id" []
      :return StoredCOA
      :body [coa NewCOA {:description "an updated COA"}]
      :summary "Updates a COA"
      :path-params [id :- s/Str]
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :create-coa
      :login login
      (ok (flows/update-flow :get-fn #(read-coa @coa-store %)
                             :realize-fn realize-coa
                             :update-fn #(update-coa @coa-store (:id %) %)
                             :entity-type :coa
                             :id id
                             :login login
                             :entity coa)))
    (GET "/:id" []
      :return (s/maybe StoredCOA)
      :summary "Gets a COA by ID"
      :path-params [id :- s/Str]
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :read-coa
      (if-let [d (read-coa @coa-store id)]
        (ok d)
        (not-found)))
    (DELETE "/:id" []
      :no-doc true
      :path-params [id :- s/Str]
      :summary "Deletes a COA"
      :header-params [api_key :- (s/maybe s/Str)]
      :capabilities :delete-coa
      :login login
      (if (flows/delete-flow :get-fn #(read-coa @coa-store %)
                             :delete-fn #(delete-coa @coa-store %)
                             :entity-type :coa
                             :id id
                             :login login)
        (no-content)
        (not-found))))
  (context "/coas" []
    :tags ["COA"]
    (POST "/" []
      :return [c/ID]
      :body [coas [NewCOA] {:description "a list of new COA"}]
      :header-params [api_key :- (s/maybe s/Str)]
      :summary "Adds a list of new COA"
      :capabilities :create-coa
      :login login
      (ok (map (fn [coa]
                 (-> (flows/create-flow :entity-type :coa
                                        :realize-fn realize-coa
                                        :store-fn #(create-coa @coa-store %)
                                        :login login
                                        :entity coa)
                     :id))
               coas)))))
