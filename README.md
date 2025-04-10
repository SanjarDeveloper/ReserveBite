# ReserveBite - Restaurant Management System

![ReserveBite Waiter Dashboard](https://via.placeholder.com/800x400.png?text=ReserveBite+Waiter+Dashboard)  
*Screenshot of the Waiter Dashboard (replace with an actual screenshot URL from your repository)*

## Overview

ReserveBite is a web-based restaurant management system designed to streamline operations for restaurant staff, with a focus on waiters. The application provides a dynamic waiter dashboard to manage active orders, create new orders, and edit existing ones—all within a single-page interface. Built with Spring Boot and Thymeleaf, ReserveBite leverages AJAX for real-time updates, ensuring a seamless user experience without page reloads.

The waiter dashboard allows waiters to:
- View active orders in a card view.
- Create new orders by selecting a table.
- Edit orders by adding or removing menu items from their restaurant.
- Complete orders with a single click.

This project is hosted on GitHub at [your-username/reservebite](https://github.com/your-username/reservebite) (replace with your actual GitHub repository URL).

## Features

- **Single-Page Waiter Dashboard**:
  - Displays active orders in a card view with details like Order ID, Table Number, Order Date, and Total Amount.
  - Allows editing of orders directly from the dashboard without navigating to a separate page.
  - Supports creating new orders via a modal and immediately adding items to them.

- **Dynamic Order Management**:
  - Create a new order by selecting a table from a dropdown (no customer selection required).
  - Add menu items from the waiter's restaurant to an order with specified quantities.
  - Remove items from an order.
  - Complete an order, removing it from the active orders list.

- **User-Friendly Interface**:
  - Clean and responsive design using Bootstrap 4.5.2.
  - Real-time updates using AJAX to avoid page reloads.
  - Styled with a modern aesthetic, featuring a red (`#e74c3c`) and green (`#28a745`) color scheme, as shown in the waiter dashboard screenshot.

- **Error Handling**:
  - Gracefully handles missing data (e.g., empty order items) with fallbacks in JavaScript.
  - Provides basic error alerts for failed operations, with room for enhancement.

## Technologies Used

- **Backend**:
  - **Spring Boot**: Framework for building the RESTful API and handling business logic.
  - **Spring Security**: For authentication and authorization (secures `/waiter/**` endpoints for `ROLE_WAITER`).
  - **Spring Data JPA**: For database interactions with Hibernate as the ORM.
  - **Thymeleaf**: For server-side rendering of the initial dashboard page.
  - **Jackson**: For JSON serialization/deserialization.

- **Frontend**:
  - **HTML/CSS/JavaScript**: Core technologies for the user interface.
  - **Bootstrap 4.5.2**: For responsive design and styling.
  - **Font Awesome 5.15.4**: For icons (e.g., dashboard, logout, edit).
  - **jQuery 3.5.1**: For AJAX requests and DOM manipulation.

- **Database**:
  - MySQL/PostgreSQL: For storing entities like `Order`, `OrderItem`, `Menu`, `Table`, `Waiter`, and `Restaurant`.

## Prerequisites

- **Java 17** or later
- **Maven**: For dependency management and building the project
- **MySQL/PostgreSQL**: For the database (configure in `application.properties`)
- **Git**: For version control

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/SanjarDeveloper/reservebite.git
cd reservebite
java reservebite
