# SocialWine

Application:
- [x] Scraper
- [x] implement scraper on app
- [x] finish the menu
- [x] implement auto update of the review scraped on mongo db in the two collection (user , winery) ----> the winery collection is missing
- [ ] implement auto adding of the user scraped on graph db using the function addPostComplete and addUser  --> BASHAR
- [ ] create the login with a search in mongo db to login and connect to menu --> LEO
- [ ] fix the menu the add post with operation in mongo -->DOMENICO


Mongo Db:

- [x] Crud in mongo Db
- [x] 1 advanced mongo db
- [x] 2 advanced mongo db
- [x] 3 advanced mongo db
- [x] test the mongo db query 

Graph Db:
- [x] Crud in Graph
- [x] 1 advanced og graph ----> TESTED
- [x] 2 advanced on graph ---->TESTED
- [x] Create query ADD_POST where you specify the winery, the title, the description, the creator. In order to create the "created" and "Belong" relationships ----> TESTED
- [x] create the random follow between user (a function that admin can launch)[randomFollowByUser] ----> TESTED
- [x] create the random like between user and post (a function that admin can launch) [randomLikeByUser] ----> TESTED
- [x] Testing the graph advanced query
- [x] create a function to see all friends in graph db 
- [ ] populating the graph db with user and post scanning the review in mongo db -----> TO BE TESTED ---> BASHAR 





Distruibuited : 
- [ ] Create the replica on the linux servers ---> DOMENICO

  
  
## Population function:
-we use random function to create "follow" relationships between users [to be used only once]
- we use the random function to create "like" relationships between users and posts [to be used only once]
- the add review function will take as input = (title, description, author, linked winery) and create the "created" relationship between the post and the user, and the "belong" relationship between the post and the PageWinery [to be used when a user wants to write a review]
  
  

# Questions and answers
1. we can delete the second collection and leave only reviews why do we do all the advanced queries on mongo on review?
  we can leave becouse there are no costrains
2. We don't have any nested document, should we implement it in the winery document?
  Yes we implement it but we do not make any queries because it is not in the costrains
3. we dont use the beans in our code becouse we use directly the query to do operation! We can delete the beans?
  No, we leaeve on the project, but we dont use often becouse we dont need to manipulate a lot our object java like Javascript
4. when a user login to mantain the user name we can use the key value database 
  key value solution 
  

Link for Neo4j:
https://github.com/andrealagna/FUTDataMining/tree/08253590b90f68caf3d70c00e288dedeb94394ac/FUTPersistence/src/main/java/neo4j



