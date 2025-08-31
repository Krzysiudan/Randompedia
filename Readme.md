Repositories are implemented in the data layer. 
Their interfaces live in the domain layer so the UI depends on domain contracts, not data details.
This follows Clean Architecture’s dependency rule while aligning with Google’s guidance that repositories encapsulate data logic