# PaymentMethodsPromotions
[//]: # (## Project's purpose)

[//]: # (* This project is supposed to calculate the most profitable way of paying for a list of orders, saving as much money as possible thanks to embracing discounts.)

[//]: # (* Here is a set of rules it must follow:)

[//]: # (  * Every order has a set of discounts available assigned to it)

[//]: # (  * There are )

[//]: # (  * If the whole order is paid using one card, which has a discount assigned to it, then it can be embraced for that order.)

[//]: # (  * If at least 10%, but not the whole of the order's value is paid for using points, then there is a 10% discount available.)
## Project's structure:
* Whole source code is located in `src/main/java/projects`
  * It consists of the following classes:
    * `Main` — used to run the program
    * `CalculationsClass` — responsible for main program logic and calculating the result
    * `JsonFileReader` — used to read data from `JSON` files
    * `Order` — representing orders read from a file
    * `PaymentMethod` — representing payment methods read from a file
    * `PaymentAssignmentOption` — used to represent different options of payment for given orders
    * `Enum AssignmentType` — used to determine what type of payment has been assigned to a given `PaymentAssignmentOption`
* `JSON` files that are used by the program to load data are located in `src/main/resources/data`
* Unit tests are located in `src/test/java`
  * For convenience there is also a test suite present in that folder to allow all tests to be run simultaneously
  * Additional files that are used for unit testing are located in `src/test/data`

### All functions of the program have been described in the source code using comments
### JAR file is located under `out/artifacts/PaymentMethodsPromotions_jar/PaymentMethodsPromotions.jar`