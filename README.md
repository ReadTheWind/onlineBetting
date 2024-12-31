# onlineBetting
> stores and provides betting offer's stakes for different customers, with the capability to return the highest stakes on a betting offer

## session 
- Choosing the combination of customerId and timestamp can facilitate and expedite the session expiration check, and at the same time, it can save some space compared to storing the expiration time.
- Obtaining a new session will simultaneously write data into the minute and customer correspondence table to avoid the need for a full scan when clearing expired sessions, thereby enhancing performance.
- Use asynchronous scheduling to clear expired sessions.
- Use concurrent containers for thread safety.

## bet offer's stakes
- Use the jump table structure to store top 20 bets, and only the highest stake per betting offer counts for a specific customer.
- Use concurrent containers for thread safety.