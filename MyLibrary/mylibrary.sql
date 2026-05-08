DROP DATABASE IF EXISTS MyLibrary;
CREATE DATABASE MyLibrary;
USE MyLibrary;


CREATE TABLE userinfo (
    userId INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    userType INT NOT NULL CHECK (userType IN (1, 2)) -- 1: Admin, 2: Reader
);


CREATE TABLE authors (
    authorId INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    surname VARCHAR(50) NOT NULL,
    website VARCHAR(100) NOT NULL
);


CREATE TABLE books (
    bookId INT AUTO_INCREMENT PRIMARY KEY,
    authorId INT NOT NULL,
    title VARCHAR(100) NOT NULL,
    year INT,
    numberOfPages INT,
    cover VARCHAR(255),
    about TEXT,
    `read` INT CHECK (`read` BETWEEN 1 AND 3),
    rating INT CHECK (rating BETWEEN 0 AND 5),
    comments TEXT,
    releaseDate DATE,
    FOREIGN KEY (authorId) REFERENCES authors(authorId) ON DELETE CASCADE
);


DELIMITER //
CREATE TRIGGER trg_delete_orphan_authors
AFTER DELETE ON books
FOR EACH ROW
BEGIN
    DECLARE book_count INT;
    SELECT COUNT(*) INTO book_count FROM books WHERE authorId = OLD.authorId;
    IF book_count = 0 THEN
        DELETE FROM authors WHERE authorId = OLD.authorId;
    END IF;
END;
//
DELIMITER ;


-- FUNCTION: getUpcomingWishlistCount
DELIMITER //
CREATE FUNCTION getUpcomingWishlistCount()
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE upcoming_count INT;
    SELECT COUNT(*) INTO upcoming_count
    FROM books
    WHERE `read` = 3 AND releaseDate BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY);
    RETURN upcoming_count;
END;
//
DELIMITER ;


DELIMITER //
CREATE PROCEDURE AddBook(
    IN p_title VARCHAR(255),
    IN p_authorId INT,
    IN p_year INT,
    IN p_pages INT,
    IN p_description TEXT,
    IN p_read INT,
    IN p_rating INT,
    IN p_comments TEXT,
    IN p_coverPath VARCHAR(500)
)
BEGIN
    INSERT INTO books (title, authorId, year, pages, description, `read`, rating, comments, coverPath) 
    VALUES (p_title, p_authorId, p_year, p_pages, p_description, p_read, p_rating, p_comments, p_coverPath);
END //
DELIMITER ;


-- FUNCTION: GetBookDetailsById
DELIMITER //
CREATE FUNCTION GetBookDetailsById(in_bookId INT)
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE bookInfo TEXT;
    SELECT CONCAT('Title: ', title, ', Author ID: ', authorId, ', Year: ', year)
    INTO bookInfo
    FROM books
    WHERE bookId = in_bookId;
    RETURN bookInfo;
END;
//
DELIMITER ;

-- FUNCTION: SearchAuthorByName
DELIMITER //
CREATE FUNCTION SearchAuthorByName(in_authorName VARCHAR(50))
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE authorInfo TEXT;
    SELECT CONCAT('Author: ', name, ' ', surname, ', Website: ', website)
    INTO authorInfo
    FROM authors
    WHERE name = in_authorName
    LIMIT 1;
    RETURN authorInfo;
END;
//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetFavoriteBooks()
BEGIN
    SELECT 
        b.title AS book_title,
        a.name AS author_name,
        b.rating
    FROM books b
    JOIN authors a ON b.authorId = a.authorId
    WHERE b.rating >= 4 AND b.rating <= 5
    ORDER BY b.rating DESC, b.title ASC;
END //
DELIMITER ;


DELIMITER $$
CREATE PROCEDURE AddBookToWishlist(IN book_id_param INT)
BEGIN
    UPDATE books SET books.`read` = 3 WHERE books.bookId = book_id_param;
END$$
DELIMITER ;

-- PROCEDURE: DeleteBookById
DELIMITER //
CREATE PROCEDURE DeleteBookById(in_bookId INT)
BEGIN
    DELETE FROM books WHERE bookId = in_bookId;
END;
//
DELIMITER ;

-- FUNCTION: SearchBooks
DELIMITER //
CREATE FUNCTION SearchBooks(in_query VARCHAR(100))
RETURNS TEXT
DETERMINISTIC
BEGIN
    DECLARE results TEXT;
    SELECT GROUP_CONCAT(title SEPARATOR '; ')
    INTO results
    FROM books
    WHERE title LIKE CONCAT('%', in_query, '%')
       OR about LIKE CONCAT('%', in_query, '%')
       OR comments LIKE CONCAT('%', in_query, '%');
    RETURN results;
END;
//
DELIMITER ;

-- PROCEDURE: RateBook
DELIMITER //
CREATE PROCEDURE RateBook(in_bookId INT, in_rating INT)
BEGIN
    UPDATE books
    SET rating = in_rating
    WHERE bookId = in_bookId;
END;
//
DELIMITER ;

-- PROCEDURE: UpdateBookDetails
DELIMITER //
CREATE PROCEDURE UpdateBookDetails(
    IN in_bookId INT,
    IN in_title VARCHAR(100),
    IN in_year INT,
    IN in_pages INT,
    IN in_rating INT,
    IN in_comments TEXT
)
BEGIN
    UPDATE books
    SET title = in_title,
        year = in_year,
        numberOfPages = in_pages,
        rating = in_rating,
        comments = in_comments
    WHERE bookId = in_bookId;
END;
//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetFavoriteAuthors()
BEGIN
    SELECT 
        a.name AS author_name,
        COUNT(b.bookId) AS book_count
    FROM authors a
    JOIN books b ON a.authorId = b.authorId
    GROUP BY a.authorId, a.name
    HAVING COUNT(b.bookId) >= 3
    ORDER BY book_count DESC, a.name ASC;
END //
DELIMITER ;


-- FUNCTION: CheckLogin
DELIMITER //
CREATE FUNCTION CheckLogin(in_username VARCHAR(50), in_password VARCHAR(100))
RETURNS INT
DETERMINISTIC
BEGIN
    DECLARE uid INT;
    SELECT userId INTO uid
    FROM userinfo
    WHERE username = in_username AND password = in_password;
    RETURN uid;
END;
//
DELIMITER ;

DELIMITER //
CREATE PROCEDURE GetUnreadBooks()
BEGIN
    SELECT 
        b.bookId,
        b.title AS bookName,
        a.name AS author,
        b.`read` AS status
    FROM books b
    JOIN authors a ON b.authorId = a.authorId
    WHERE b.`read` = 2
    ORDER BY b.title;
END //
DELIMITER ;


-- FUNCTION: GetBookCoverPath
DELIMITER //
CREATE FUNCTION GetBookCoverPath(in_bookId INT)
RETURNS VARCHAR(255)
DETERMINISTIC
BEGIN
    DECLARE path VARCHAR(255);
    SELECT cover INTO path FROM books WHERE bookId = in_bookId;
    RETURN path;
END;
//
DELIMITER ;



DELIMITER //

CREATE PROCEDURE GetBookDetailsById_Full (
    IN in_bookId INT
)
BEGIN
    SELECT title, author, year, pages, about, status, rating, comments, cover
    FROM books
    WHERE bookId = in_bookId;
END;
//
DELIMITER ;




INSERT INTO userinfo (userId, username, password, userType) VALUES
(1, 'admin1', 'pass123', 1),
(2, 'reader1', 'pass456', 2);


INSERT INTO authors (authorId, name, surname, website) VALUES
(1, 'George', 'Orwell', 'website-1'),
(2, 'Jane', 'Austen', 'website-2');


INSERT INTO books (bookId, authorId, title, year, numberOfPages, cover, about, `read`, rating, comments, releaseDate) VALUES
(1, 1, '1984', 1949, 328, 'Book1.jpg', 'Dystopian novel', 1, 5, 'Excellent book', NULL),
(2, 2, 'Pride and Prejudice', 1813, 279, 'Book2.jpg', 'Classic romance novel', 2, 0, NULL, NULL),
(3, 1, 'Animal Farm', 1945, 112, 'Book3.jpg', 'Political satire', 3, 0, NULL, CURDATE() + INTERVAL 5 DAY);


-- Yazarlar
INSERT INTO authors (authorId, name, surname, website) VALUES
(1, 'George', 'Orwell', 'website-1'),
(2, 'Jane', 'Austen', 'website-2'),
(3, 'New', 'Author', 'website-3');

-- Kitaplar
INSERT INTO books (bookId, authorId, title, year, numberOfPages, cover, about, `read`, rating, comments, releaseDate) VALUES
(1, 1, '1984', 1949, 328, 'images/1984.jpg', 'Dystopian novel', 1, 5, 'Excellent book', NULL),
(2, 1, 'Animal Farm', 1945, 112, 'images/Animal_Farm.jpg', 'Political satire', 1, 4, 'Sharp allegory', NULL),
(3, 1, 'Homage to Catalonia', 1938, 232, 'images/Homage_to_Catalonia.jpg', 'War memoir', 1, 4, NULL, NULL),
(4, 1, 'Down and Out in Paris', 1933, 213, 'images/Down_and_Out_in_Paris.jpg', 'Memoir on poverty', 2, 0, NULL, NULL),

(5, 2, 'Emma', 1815, 300, 'images/Emma.jpg', 'Classic novel', 1, 5, NULL, NULL),
(6, 2, 'Sense and Sensibility', 1811, 279, 'images/Sense_and_Sensibility.jpg', 'Romantic fiction', 1, 4, 'Enjoyable', NULL),
(7, 2, 'Persuasion', 1818, 250, 'images/Persuasion.jpg', 'Final novel', 2, 0, NULL, NULL),
(8, 2, 'Mansfield Park', 1814, 350, 'images/Mansfield_Park.jpg', 'Moral complexity', 3, 0, NULL, NULL),

(9, 3, 'Test Book A', 2020, 200, 'images/Test_Book_A.jpg', 'General test book A', 1, 3, NULL, NULL),
(10, 3, 'Test Book B', 2020, 220, 'images/Test_Book_B.jpg', 'General test book B', 2, 0, NULL, NULL),
(11, 3, 'Future Read Book', 2024, 210, 'images/Future_Read_Book.jpg', 'Upcoming release', 3, 0, NULL, CURDATE() + INTERVAL 6 DAY);



