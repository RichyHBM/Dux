Data Store
==========

The data store service is a simple online key-value based storage service

It provides a simple API for interacting with it

POST    /v1/put?name=foo&data=bar

    Parameters:
        name: String, key to store under
        data: String, value to store

    Returns:
        None


GET     /v1/get?name=foo

    Parameters:
        name: String, key to fetch
    
    Returns:
        String, the value stored under 'name'
