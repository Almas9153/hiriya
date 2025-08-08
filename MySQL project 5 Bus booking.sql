USE BusBooking;

DROP TABLE IF EXISTS Payments;
DROP TABLE IF EXISTS Bookings;
DROP TABLE IF EXISTS Customers;
DROP TABLE IF EXISTS Routes;
DROP TABLE IF EXISTS Buses;

CREATE TABLE Buses (
    BusID INT PRIMARY KEY,
    BusNumber VARCHAR(20),
    BusType VARCHAR(50),
    TotalSeats INT,
    AvailableSeats INT
);

CREATE TABLE Routes (
    RouteID INT PRIMARY KEY,
    Source VARCHAR(50),
    Destination VARCHAR(50),
    DepartureTime TIME,
    ArrivalTime TIME,
    Fare DECIMAL(10,2)
);

CREATE TABLE Customers (
    CustomerID INT PRIMARY KEY,
    Name VARCHAR(100),
    PhoneNumber VARCHAR(15),
    Email VARCHAR(100)
);

CREATE TABLE Bookings (
    BookingID INT PRIMARY KEY AUTO_INCREMENT,
    CustomerID INT,
    BusID INT,
    RouteID INT,
    SeatsBooked INT,
    TotalFare DECIMAL(10,2),
    Status VARCHAR(20),
    BookingDate DATE,
    FOREIGN KEY (CustomerID) REFERENCES Customers(CustomerID),
    FOREIGN KEY (BusID) REFERENCES Buses(BusID),
    FOREIGN KEY (RouteID) REFERENCES Routes(RouteID)
);

CREATE TABLE Payments (
    PaymentID INT PRIMARY KEY AUTO_INCREMENT,
    BookingID INT,
    AmountPaid DECIMAL(10,2),
    PaymentDate DATE,
    FOREIGN KEY (BookingID) REFERENCES Bookings(BookingID)
);

INSERT INTO Buses VALUES
(1, 'MH12AB1234', 'AC Sleeper', 40, 40),
(2, 'KA10XY5678', 'Non-AC Seater', 50, 50);

INSERT INTO Routes VALUES
(101, 'Mumbai', 'Pune', '08:00:00', '11:00:00', 500),
(102, 'Delhi', 'Jaipur', '09:00:00', '13:00:00', 700);

INSERT INTO Customers VALUES
(1, 'Rahul Sharma', '9876543210', 'rahul@gmail.com'),
(2, 'Pooja Nair', '9123456789', 'pooja@gmail.com');

DELIMITER //

CREATE PROCEDURE BookTicket (
    IN cust_id INT,
    IN bus_id INT,
    IN route_id INT,
    IN seats INT,
    IN book_date DATE
)
BEGIN
    DECLARE fare DECIMAL(10,2);
    DECLARE available INT;
    DECLARE total DECIMAL(10,2);

    SELECT AvailableSeats INTO available FROM Buses WHERE BusID = bus_id;
    IF available >= seats THEN
        SELECT Fare INTO fare FROM Routes WHERE RouteID = route_id;
        SET total = fare * seats;

        INSERT INTO Bookings (CustomerID, BusID, RouteID, SeatsBooked, TotalFare, Status, BookingDate)
        VALUES (cust_id, bus_id, route_id, seats, total, 'Confirmed', book_date);

        UPDATE Buses SET AvailableSeats = AvailableSeats - seats WHERE BusID = bus_id;
    ELSE
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Not enough seats available';
    END IF;
END;
//

CREATE TRIGGER PreventOverbooking
BEFORE INSERT ON Bookings
FOR EACH ROW
BEGIN
    DECLARE available INT;
    SELECT AvailableSeats INTO available FROM Buses WHERE BusID = NEW.BusID;
    IF NEW.SeatsBooked > available THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Overbooking not allowed';
    END IF;
END;
//

DELIMITER ;

CALL BookTicket(1, 1, 101, 2, CURDATE());
CALL BookTicket(2, 2, 102, 1, CURDATE());

SELECT 
    b.BookingID,
    c.Name AS CustomerName,
    bs.BusNumber,
    bs.BusType,
    r.Source,
    r.Destination,
    r.DepartureTime,
    r.ArrivalTime,
    b.SeatsBooked,
    b.TotalFare,
    b.Status,
    b.BookingDate
FROM Bookings b
JOIN Customers c ON b.CustomerID = c.CustomerID
JOIN Buses bs ON b.BusID = bs.BusID
JOIN Routes r ON b.RouteID = r.RouteID;




