package miscellaneous;


import com.mongodb.client.*;
import databases.advanced_graph;
import databases.advanced_mongo;
import databases.crud_graph;
import databases.crud_mongo;
import scraping.InitTh;
import scraping.scraperThread;
import static com.mongodb.client.model.Filters.eq;

import java.util.Scanner;

public class Menu {


    public void MainMenu() throws Exception {
        loginAdmin login = new loginAdmin();
        InitTh thread = new InitTh();
        thread.initThread();
        MongoClient mongoClient = MongoClients.create();
        MongoDatabase database = mongoClient.getDatabase("wine");
        scraperThread scraper = new scraperThread();
        crud_mongo mongo = new crud_mongo();
        advanced_mongo adv = new advanced_mongo();
        crud_graph graph = new crud_graph("bolt://localhost:7687", "neo4j", "root");
        advanced_graph advgraph = new advanced_graph("bolt://localhost:7687", "neo4j", "root");

        System.out.println("\n***SOCIAL WINE APPLICATION***\n");

        while (true) {
            System.out.println("\nAre you user or admin?");
            System.out.println("1" + " Admin");
            System.out.println("2" + " User");
            System.out.println("0" + " Terminate program");
            Scanner scanA = new Scanner(System.in);
            String nextIntString = scanA.nextLine();
            int choice = Integer.parseInt(nextIntString);

            switch (choice) {
                case 0:
                    System.out.println("Exiting program...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("This is not a valid menu option... Please try again!");
                    break;
                    //admin login
                case 1:
                    while (true) {
                        System.out.println("\nPlease insert your name and your password or press X to exit:");
                        if (login.logIn()) {
                            System.out.println("You can do this statistics:");
                            System.out.println("A" + " Top 10 countries that have most wineries in descending order");
                            System.out.println("B" + " Display top-20 wines' varieties according to their mean price");
                            System.out.println("C" + " Top-5 users with the highest average of them review scores.");
                            System.out.println("\nWhat you want to do?");
                            Scanner scan = new Scanner(System.in);
                            String next = scan.nextLine();
                            String choiceStatistics = next;

                            switch (choiceStatistics) {
                                case "A":
                                    adv.topTenCountriesWineries();
                                    break;
                                case "B":
                                    adv.topTwentyVarietiesAvgPrice();
                                    break;
                                case "C":
                                    adv.topFiveUsersHighestAvgScores();
                                    break;
                            }

                        }
                    }

                //user login
                case 2:
                    while (true) {
                        System.out.println("\nPlease insert your name and your password or press X to exit:");
                        if (login.logIn()) {
                            System.out.println("You can do these things :");
                            System.out.println("A" + " Follow your friend");
                            System.out.println("B" + " Unfollow a  friend");
                            System.out.println("C" + " Put like on a Post");
                            System.out.println("D" + " Delete like on a Post");
                            System.out.println("E" + " Add a Post on the social");
                            System.out.println("F" + " have tje list of suggested friends");
                            System.out.println("G" + " Discover the top 5 trending Post on the social");
                            System.out.println("\nWhat you want to do?");
                            Scanner scan = new Scanner(System.in);
                            String next = scan.nextLine();
                            String choiceStatistics = next;
                            String myName = null; //--> in this variable there is my name to do the query

                            switch (choiceStatistics) {
                                case "A":
                                    String userToFollow;
                                    Scanner inputA = new Scanner(System.in);
                                    System.out.println("who do you want to follow? Name: ");
                                    userToFollow = inputA.next();
                                    graph.createRelationFollow(myName,userToFollow);
                                    System.out.println("Done");
                                    break;
                                case "B":
                                    String userToUnfollow;
                                    Scanner inputB = new Scanner(System.in);
                                    System.out.println("who do you want to Unfollow? Name: ");
                                    userToUnfollow = inputB.next();
                                    graph.deleteRelationFollow(myName,userToUnfollow);
                                    System.out.println("Done");
                                    break;
                                case "C":
                                    String titlePostToPutLike;
                                    Scanner inputC = new Scanner(System.in);
                                    System.out.println("which post you want to like? TitlePost: ");
                                    titlePostToPutLike = inputC.next();
                                    graph.createRelationLike(titlePostToPutLike,myName);
                                    System.out.println("Done");
                                    break;
                                case "D":
                                    String titlePostToDeleteLike;
                                    Scanner inputD = new Scanner(System.in);
                                    System.out.println("which post you want to delete like? TitlePost: ");
                                    titlePostToDeleteLike = inputD.next();
                                    graph.deleteRelationLike(titlePostToDeleteLike,myName);
                                    System.out.println("Done");
                                    break;
                                case "E":
                                    System.out.println("which post you want to delete like? TitlePost: ");
                                    String titleofthepost;
                                    Scanner inputF = new Scanner(System.in);
                                    titleofthepost = inputF.next();
                                    String descriptionofthepost;
                                    Scanner inputG = new Scanner(System.in);
                                    descriptionofthepost = inputG.next();
                                    String wineryName;
                                    Scanner inputE = new Scanner(System.in);
                                    wineryName = inputG.next();
                                    graph.addPostComplete(myName,titleofthepost,descriptionofthepost,wineryName);
                                    System.out.println("Done");
                                    break;
                                case "F":
                                    System.out.println("this is the list of suggested user for you: ");
                                    advgraph.suggestedUserByFriends(myName);
                                    break;
                                case "G":
                                    System.out.println("this is the list of the tranding post on SocialWine ");
                                    advgraph.FiveMostLikePost();
                                    break;

                            }

                        }
                    }



                    /* switch (choiceB) {
                            case "A":
                                String fName;
                                String lName;
                                String biography;
                                System.out.println("Insert the name, the last name and the biography of the author");
                                Scanner inputA = new Scanner(System.in);
                                System.out.println("Name:");
                                fName = inputA.next();
                                System.out.println("Surname:");
                                lName = inputA.next();
                                System.out.println("Biography:");
                                biography = inputA.next();
                                AuthorDao.insertAuthor(fName, lName, biography);
                                inputA.close();
                                break;
                            case "B":
                                int id;
                                System.out.println("Insert the identifier of the author (drop the author will drop even his books)");
                                Scanner inputB = new Scanner(System.in);
                                System.out.println("Identifier:");
                                id = inputB.nextInt();
                                misc.deleteAutCascade(id);
                                inputB.close();
                                break;
                            case "C":
                                System.out.println("Authors list:");
                                List<Author> listaAuthor = AuthorDao.getAllAuthor();
                                for (Author author : listaAuthor) {
                                    System.out.println("Id author: " + author.getAuthor_id() + "   Name: " + author.getFname() + "   Surname: " + author.getLname());
                                }
                                break;
                            case "D":
                                String name = null;
                                String location = null;
                                System.out.println("Insert the name and the location of the new publisher");
                                Scanner inputD = new Scanner(System.in);
                                System.out.println("Name:");
                                name = inputD.next();
                                System.out.println("Location:");
                                location = inputD.next();
                                PublisherDao.insertPublisher(name, location);
                                inputD.close();
                                break;
                            case "E":
                                int idPub;
                                System.out.println("Insert the identifier of the publisher (drop the publisher will drop even his books)");
                                Scanner inputE = new Scanner(System.in);
                                System.out.println("Identifier:");
                                idPub = inputE.nextInt();
                                misc.deletePubCascade(idPub);
                                inputE.close();
                                break;
                            case "F":
                                System.out.println("Publisher list:");
                                List<Publisher> listaPublisher = PublisherDao.getAllPublisher();
                                for (Publisher publisher : listaPublisher) {
                                    System.out.println("Id publisher: " + publisher.getPublisher_id() + "   Name: " + publisher.getName() + "   Location: " + publisher.getLocation());
                                }
                                break;
                            case "G":
                                String title;
                                Float price = null;
                                String category;
                                int pubblicationYear;
                                int numPages;
                                Integer idPublisher;
                                int quantity;
                                Integer idAuthor;
                                System.out.println("Insert the features of the new book");
                                Scanner inputG = new Scanner(System.in);
                                System.out.println("Title:");
                                title = inputG.nextLine();
                                System.out.println("Price:");
                                price = inputG.nextFloat();
                                System.out.println("Category:");
                                category = inputG.nextLine();
                                System.out.println("Year of publication:");
                                pubblicationYear = inputG.nextInt();
                                System.out.println("Number of pages:");
                                numPages = inputG.nextInt();
                                System.out.println("Publisher:");
                                idPublisher = inputG.nextInt();
                                System.out.println("Quantity");
                                quantity = inputG.nextInt();
                                System.out.println("Author");
                                idAuthor = inputG.nextInt();

                                try {
                                    if (misc.checkAuthor(idAuthor)) {
                                        if (misc.checkPublisher(idPublisher)) {
                                            BookDao.insertBook(title, price, category, pubblicationYear, numPages, idPublisher, quantity, idAuthor);
                                        } else {
                                            throw new AuthorPublisherNotFoundException("Please add the publisher or the author in respective section before inserting a book with them!");
                                        }
                                    } else {
                                        throw new AuthorPublisherNotFoundException("Please add the publisher or the author in respective section before inserting a book with them!");
                                    }
                                } catch (Exception e) {
                                    throw new AuthorPublisherNotFoundException("Please add the publisher or the author in respective section before inserting a book with them!");
                                }
                                inputG.close();
                                break;
                            case "H":
                                int idB;
                                System.out.println("Insert the identifier of the book that you want to drop: ");
                                Scanner inputH = new Scanner(System.in);
                                System.out.println("Identifier:");
                                idB = inputH.nextInt();
                                BookDao.deleteBook(idB);
                                inputH.close();
                            case "I":
                                System.out.println("Books list:");
                                List<Book> listaBook = BookDao.getAllBooks();
                                for (Book book : listaBook) {
                                    System.out.println("Id Book: " + book.getBook_id() + "   Title: " + book.getTitle() + "   Price: " + book.getPrice()
                                            + "  Category:  " + book.getCategory() + "  Year of publication: " + book.getYear() + "  Number of pages:  "
                                            + book.getNumPage() + "  Publisher: " + misc.publisherName(book.getPublisher_id()) + "  Quantity:  "
                                            + book.getQuantity() + "  Author: " + misc.authorName(book.getAuthor_id()));
                                }
                                break;
                            case "L":
                                int qty; int idBook;
                                System.out.println("Insert the ID of the book that you want to update with the new quantity in stock:");
                                Scanner inputL = new Scanner(System.in);
                                System.out.println("Identifier:");
                                idBook = inputL.nextInt();
                                System.out.println("Qty:");
                                qty=inputL.nextInt();
                                BookDao.modifyQuantity(idBook,qty);
                                inputL.close();
                                break;
                            case "X":
                                System.out.println("Exiting program...");
                                System.exit(0);
                                break;
                            default:
                                System.out.println("This is not a valid menu option... Please try again!");
                                break;
                        }
                    }
                case 2:
                    ldb.openDB();
                    while(true) {
                        System.out.println("\nPOSSIBILE ACTION:");
                        System.out.println("A" + " Insert new author");
                        System.out.println("B" + " Delete an author");
                        System.out.println("C" + " Browse authors");
                        System.out.println("D" + " Insert a new publisher");
                        System.out.println("E" + " Delete a publisher");
                        System.out.println("F" + " Browse publishers");
                        System.out.println("G" + " Insert a new book");
                        System.out.println("H" + " Delete a book");
                        System.out.println("I" + " Browse books");
//                      System.out.println("L" + " Update quantity of a book(+/-)");
                        System.out.println("X" + " Terminate program");

                        System.out.println("\nWhat you want to do?(LevelDB)");
                        Scanner inputC = new Scanner(System.in);
                        String nextIntStringC = inputC.nextLine();
                        String choiceC = nextIntStringC;

                        switch (choiceC) {
                            //prova se vedere se funziona, per non iserire manualmente ogni volta i valori
                            case "A"://FUNZIONA INSERT AUTHOR
                                System.out.println("Insert the name, the surname and the age of the new publisher");
                                Scanner inputA = new Scanner(System.in);
                                System.out.println("Key (author:x:Value):");
                                String keyAuth = inputA.next();
                                if(keyAuth.split(":")[0].equals("author")){
                                    System.out.println("Value (name,surname,age):");
                                    String authInfo = inputA.next();
                                    ldb.putAsString(keyAuth, authInfo);
                                }else{
                                    throw new Exception("Key not valid, must be an author");
                                }

                                break;

                            case "B"://FUNZIONA DELETE AUTHOR
                                System.out.println("Insert the identifier of the author that you want to drop:");
                                System.out.println("Key:");
                                Scanner inputB = new Scanner(System.in);
                                String key = inputB.nextLine();
                                ldb.deleteValue(key);
                                break;

                            case "C": //FUNZIONA LIST AUTHOR
                                System.out.println("Author list:");
                                //creo una stringa dei risultati e poi faccio output
                                HashMap<String, String> entries = ldb.findByPrefix("author:");
                                System.out.println(entries);
                                break;

                            case "D": //FUNZIONA
                                System.out.println("Insert the name and the location of the new publisher");
                                Scanner inputD = new Scanner(System.in);
                                System.out.println("Key (publisher:x:Value):");
                                String keyPub = inputD.next();
                                if(keyPub.split(":")[0].equals("publisher")) {
                                    System.out.println("Value (name,location):");
                                    String value = inputD.next();
                                    ldb.putAsString(keyPub, value);
                                }else
                                    throw new Exception("Key not valid, must be a publisher");

                                break;

                            case "E": //FUNZIONA
                                String idPub;
                                System.out.println("Insert the key of the publisher that you want to drop:");
                                Scanner inputE = new Scanner(System.in);
                                idPub = inputE.next();
                                ldb.deleteValue(idPub);
                                break;

                            case "F": //FUNZIONA
                                System.out.println("Publisher list:");
                                List<String> publent = ldb.findValuesByPrefix("publisher:");
                                System.out.println(publent);
                                break;

                            case "G"://FUNZIONA
                                System.out.println("Insert the features of the new book");
                                System.out.println("Key (book:x:Value):");
                                Scanner inputG = new Scanner(System.in);
                                String keyBook = inputG.next();
                                if(keyBook.split(":")[0].equals("book")) {
                                    System.out.println("Value (Title,Price,Category,Year,Number of pages,Publisher,Quantity,Author):");
                                    String value1 = inputG.next();
                                    ldb.putAsString(keyBook, value1);
                                }else
                                    throw new Exception("Key not valid, must be a book");
                                break;

                            case "H"://FUNZIONA
                                String idBook;
                                System.out.println("Insert the key of the book that you want to drop:");
                                Scanner inputH = new Scanner(System.in);
                                idBook = inputH.next();
                                ldb.deleteValue(idBook);
                                break;

                            case "I"://FUNZIONA
                                System.out.println("Book list:");
                                List<String> booklist = ldb.findValuesByPrefix("book:");
                                System.out.println(booklist);
                                break;

                       *//* case "L":
                            System.out.println("Book list:");
                            //prendo la lista dei libri
                            List<String> Klist = ldb.findKeysByPrefix("book:");
                            System.out.println(Klist);
                            //controllo se è vuoto
                            if (Klist.isEmpty())
                                throw new Exception("Impossible to update quantity. No books detected. Please add one.");
                            //id del book che voglio aggiornare
                            Scanner input7 = new Scanner(System.in);
                            System.out.println("Insert the ID of the book that you want to update with the new quantity in stock:");
                            String keyBook1 = input7.next();
                            //quantità che voglio scrivere
                            System.out.println("Insert the new quantity for the book:");
                            Integer qty1 = input7.nextInt();
                            //richiamo la funzione dell'update
                            ldb.deleteValue(input7);


                            //ritorno la lista dei libri con la quantità aggiornata
                            System.out.println("Book list:");
                            List<String> blistupd = ldb.findValuesByPrefix("book:");
                            System.out.println(blistupd);
                            break; *//*

                            case "X":
                                System.out.println("Exiting program...");
                                System.exit(0);
                                break;
                            default:
                                System.out.println("This is not a valid menu option... Please try again!");
                                break;
                        }

                    }
                case 3: //PER MONGO - RIVEDERE TUTTI I CASI
                    mongo.mongoConnection();
                    while(true) {
                        System.out.println("\nPOSSIBILE ACTION:");
                        System.out.println("A" + " Insert new author");
                        System.out.println("B" + " Delete an author");
                        System.out.println("C" + " Browse authors");
                        System.out.println("D" + " Insert a new publisher");
                        System.out.println("E" + " Delete a publisher");
                        System.out.println("F" + " Browse publishers");
                        System.out.println("G" + " Insert a new book");
                        System.out.println("H" + " Delete a book");
                        System.out.println("I" + " Browse books");
//                      System.out.println("L" + " Update quantity of a book(+/-)");
                        System.out.println("W" + "Show all the document");
                        System.out.println("X" + " Terminate program");

                        System.out.println("\nWhat you want to do?(LevelDB)");
                        Scanner inputC = new Scanner(System.in);
                        String nextIntStringC = inputC.nextLine();
                        String choiceC = nextIntStringC;

                        switch (choiceC) {
                            //prova se vedere se funziona, per non iserire manualmente ogni volta i valori
                            case "A"://FUNZIONA INSERT AUTHOR
                                System.out.println("Insert the name, the surname and the age of the new publisher");
                                Scanner inputA = new Scanner(System.in);
                                System.out.println("Key (author:x:Value):");
                                String keyAuth = inputA.next();
                                if (keyAuth.split(":")[0].equals("author")) {
                                    System.out.println("Value (name,surname,age):");
                                    String authInfo = inputA.next();
                                    ldb.putAsString(keyAuth, authInfo);
                                } else {
                                    throw new Exception("Key not valid, must be an author");
                                }

                                break;

                            case "B"://FUNZIONA DELETE AUTHOR
                                System.out.println("Insert the identifier of the author that you want to drop:");
                                System.out.println("Key:");
                                Scanner inputB = new Scanner(System.in);
                                String key = inputB.nextLine();
                                ldb.deleteValue(key);
                                break;

                            case "C": //FUNZIONA LIST AUTHOR
                                System.out.println("Author list:");
                                //creo una stringa dei risultati e poi faccio output
                                HashMap<String, String> entries = ldb.findByPrefix("author:");
                                System.out.println(entries);
                                break;

                            case "D": //FUNZIONA
                                System.out.println("Insert the name and the location of the new publisher");
                                Scanner inputD = new Scanner(System.in);
                                System.out.println("Key (publisher:x:Value):");
                                String keyPub = inputD.next();
                                if (keyPub.split(":")[0].equals("publisher")) {
                                    System.out.println("Value (name,location):");
                                    String value = inputD.next();
                                    ldb.putAsString(keyPub, value);
                                } else
                                    throw new Exception("Key not valid, must be a publisher");

                                break;

                            case "E": //FUNZIONA
                                String idPub;
                                System.out.println("Insert the key of the publisher that you want to drop:");
                                Scanner inputE = new Scanner(System.in);
                                idPub = inputE.next();
                                ldb.deleteValue(idPub);
                                break;

                            case "F": //FUNZIONA
                                System.out.println("Publisher list:");
                                List<String> publent = ldb.findValuesByPrefix("publisher:");
                                System.out.println(publent);
                                break;

                            case "G"://FUNZIONA
                                System.out.println("Insert the features of the new book");
                                System.out.println("Key (book:x:Value):");
                                Scanner inputG = new Scanner(System.in);
                                String keyBook = inputG.next();
                                if (keyBook.split(":")[0].equals("book")) {
                                    System.out.println("Value (Title,Price,Category,Year,Number of pages,Publisher,Quantity,Author):");
                                    String value1 = inputG.next();
                                    ldb.putAsString(keyBook, value1);
                                } else
                                    throw new Exception("Key not valid, must be a book");
                                break;

                            case "H"://FUNZIONA
                                String idBook;
                                System.out.println("Insert the key of the book that you want to drop:");
                                Scanner inputH = new Scanner(System.in);
                                idBook = inputH.next();
                                ldb.deleteValue(idBook);
                                break;

                            case "I"://FUNZIONA
                                System.out.println("Book list:");
                                List<String> booklist = ldb.findValuesByPrefix("book:");
                                System.out.println(booklist);
                                break;

                       *//* case "L":
                            System.out.println("Book list:");
                            //prendo la lista dei libri
                            List<String> Klist = ldb.findKeysByPrefix("book:");
                            System.out.println(Klist);
                            //controllo se è vuoto
                            if (Klist.isEmpty())
                                throw new Exception("Impossible to update quantity. No books detected. Please add one.");
                            //id del book che voglio aggiornare
                            Scanner input7 = new Scanner(System.in);
                            System.out.println("Insert the ID of the book that you want to update with the new quantity in stock:");
                            String keyBook1 = input7.next();
                            //quantità che voglio scrivere
                            System.out.println("Insert the new quantity for the book:");
                            Integer qty1 = input7.nextInt();
                            //richiamo la funzione dell'update
                            ldb.deleteValue(input7);


                            //ritorno la lista dei libri con la quantità aggiornata
                            System.out.println("Book list:");
                            List<String> blistupd = ldb.findValuesByPrefix("book:");
                            System.out.println(blistupd);
                            break; *//*

                            case "X":
                                System.out.println("Exiting program...");
                                System.exit(0);
                                break;
                            default:
                                System.out.println("This is not a valid menu option... Please try again!");
                                break;
                        }
                    }
            }
        }

    }*/
            }
        }
    }
}

