
CREATE TABLE patron (patron_email VARCHAR(150) UNIQUE NOT NULL, patron_password VARCHAR(65) NOT NULL, patron_name VARCHAR(255) NOT NULL, patron_id INT(6), patron_verified BOOLEAN DEFAULT 0, books_issued INT, PRIMARY KEY(patron_id));
            
CREATE TABLE librarian (librarian_email VARCHAR(50) UNIQUE NOT NULL, librarian_password VARCHAR(65) NOT NULL, librarian_name VARCHAR(255) NOT NULL, librarian_id INT(6), librarian_verified BOOLEAN DEFAULT 0, PRIMARY KEY(librarian_id));

CREATE TABLE verify_email (user_email VARCHAR(50), verification_key VARCHAR(65), PRIMARY KEY(user_email));

CREATE TABLE book(isbn INT(13), author_name VARCHAR(255) NOT NULL, title VARCHAR(255) NOT NULL, call_number VARCHAR(150), publisher_name VARCHAR(255) NOT NULL, year_of_publication DATE, location_in_library VARCHAR(255) NOT NULL, number_of_copies INT NOT NULL, book_status BOOLEAN , image VARCHAR(255), available_copies INT NOT NULL, created_by INT NOT NULL, updated_by INT, PRIMARY KEY(isbn), FOREIGN KEY(created_by) REFERENCES librarian(librarian_id) ON DELETE NO ACTION,FOREIGN KEY(updated_by) REFERENCES librarian(librarian_id) ON DELETE SET NULL);

CREATE TABLE keywords(isbn INT(13), keyword VARCHAR(255), PRIMARY KEY(isbn, keyword), FOREIGN KEY(isbn) REFERENCES book(isbn) ON DELETE CASCADE);
    
CREATE TABLE issue_book(patron_id INT(6), isbn INT(13), issue_date DATETIME, due_date DATETIME NOT NULL, actual_return_date DATETIME, PRIMARY KEY(patron_id, isbn, issue_date), FOREIGN KEY(patron_id) REFERENCES patron(patron_id) ON DELETE NO ACTION, FOREIGN KEY(isbn) REFERENCES book(isbn) ON DELETE NO ACTION);
