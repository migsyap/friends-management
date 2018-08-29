# friends-management

This application manages friend connections and subscriptions via email for a simple social networking application.

### API endpoints
##### [![](https://img.shields.io/badge/POST-%2Fapi%2Fconnect-00b149.svg)](#)
This API is used to connect two (2) emails as friends. The request must include the JSON payload:
```
    {
        "friends": ["person1@mail.com", "person2@mail.com"]
    }
```

The friends list should contain exactly two (2) distinct non-blank items. Otherwise, a ```HTTP 406 - Not Acceptable```
error is given with the following response:
```
    {
        "success": false,
        "message: "You may only connect exactly two (2) persons."
    }
```

When friends are successfully connected, the API returns a ```HTTP 200 - OK``` response with the body:
```
    {
        "success": true
    }
```

<br>

##### [![](https://img.shields.io/badge/POST-%2Fapi%2Ffind%2Fconnections-00b149.svg)](#)
This API is used to find the list of friends an email has. It must have the following JSON payload in its request body:
```
    {
        "email": "person@mail.com"
    }
```

It does not return any error. When the email is not found or is empty, it simply returns an empty list with zero (0) count.
The response is ```HTTP 200 - OK``` with the return body:
```
	{
		"success": true,
		"friends": [
			"friend1@mail.com",
			"friend2@mail.com"
		],
		"count": 2
	}
```

<br>

##### [![](https://img.shields.io/badge/POST-%2Fapi%2Ffind%2Fcommon--connections-00b149.svg)](#)
This API is used to find the common friends between two (2) emails. The request should have the JSON in the body:
```
	{
        "friends": ["person1@mail.com", "person2@mail.com"]
    }
```

Similar to the ```/api/connect``` API, the list must have exactly two (2) distinct non-blank items or else,
```HTTP 406 - Not Acceptable``` is give with the following response:
```
    {
        "success": false,
        "message: "You may only connect exactly two (2) persons."
    }
```

A successful search yields a ```HTTP 200 - OK``` response with the following body:
```
	{
		"success": true,
		"friends": [
			"friend1@mail.com"
		],
		"count": 1
	}
```

<br>

##### [![](https://img.shields.io/badge/POST-%2Fapi%2Fsubscribe-00b149.svg)](#)
This API is used to let the requestor subscribe (or follow) updates from a target email, which may not necessarily be a friend.
The request must include the JSON payload:
```
    {
        "requestor": "requestor@mail.com",
        "target": "target@mail.com"
    }
```

When either the requestor or the target is not provided, or they are the same, a ```HTTP 406 - Not Acceptable```
error is given with the following response:
```
    {
        "success": false,
        "message: "Both the requestor and the target must be provided."
    }
```

On the event that the requestor is already subscribed to the target, ```HTTP 406 - Not Acceptable``` error is given with
the following response:
```
    {
        "success": false,
        "message: "requestor@mail.com has already subscribed to target@mail.com!"
    }
```

Upon a successful subscription, it returns ```HTTP 200 - OK``` response with the body:
```
    {
        "success": true
    }
```

<br>

##### [![](https://img.shields.io/badge/POST-%2Fapi%2Fblock-00b149.svg)](#)
This API is used to let the requestor block (or unfollow) updates from a target email, which may or may not be a friend.
The request must include the JSON payload:
```
    {
        "requestor": "requestor@mail.com",
        "target": "target@mail.com"
    }
```

When either the requestor or the target is not provided, or they are the same, a ```HTTP 406 - Not Acceptable```
error is given with the following response:
```
    {
        "success": false,
        "message: "Both the requestor and the target must be provided."
    }
```

Upon successfully blocking an email, it returns ```HTTP 200 - OK``` response with the body:
```
    {
        "success": true
    }
```

<br>

##### [![](https://img.shields.io/badge/POST-%2Fapi%2Ffind%2Frecipients-00b149.svg)](#)
This API is used to find the list of recipient emails of an update from the sender. The request must include the JSON payload:
```
    {
        "sender": "sender@mail.com",
        "text": "hello person1@mail.com and person2@mail.com"
    }
```

The list of recipients of an update is determined by the following:
```
F  +  S  +  M  -  B

where:
F - list of friends of the sender as described in the API /api/find/connections
S - list of subscribers of the sender who were linked via the API /api/subscribe
M - list of mentioned emails in the text attribute of the request
B - list of emails that the sender has either blocked or has blocked the sender (via the API /api/block)
```

When either the sender or the text is not provided, a ```HTTP 406 - Not Acceptable```
error is given with the following response:
```
    {
        "success": false,
        "message: "Both the sender and the text must be provided."
    }
```

A successful search returns ```HTTP 200 - OK``` response with the body:
```
    {
        "success": true
        "recipients": [
        	"friend1@mail.com",
        	"person1@mail.com",
        	"person2@mail.com"
        ]
    }
```
