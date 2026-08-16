# javatemplate

## Developer Guide
Please follow the below tips to use this template:
1. If you are updating an entity using `update` repository method, define a `clone()` method in the entity, clone and return the new object in the setter methods of the entity. This way, the old version of the object can still be retained in the repository store at the time `update()` method is called. That will help in using the old entity to update any index dependent on the set field. Check the parkinglot project for impl.
2. Extend the `AbstractInMemoryRepository` to create new repositories for entities. Override functions as required to create and update indices.
3. Extend the `BaseEntity` class in each entity created. This adds default fields `id`, `createdAt`, `updatedAt` and `active`.
4. Make sure to check on the `active` field of the entity while returning it in repository get methods.
5. If an index is created on a final field of the entity in its repository, we don't need to override the update method because that field will never be changed for that entity.
6. Custom maps for indices should map to the id of the entity instead of the entity object itself. This way, when an entity is updated with the clone method, we don't need to update the index value. It will directly reflect.
