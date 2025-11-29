# CMPE343-Project-2

Role-Based Contact Management System - CMPE 343 – Fall 2025/2026 – Course Project #2

This project is a console-based Java application that integrates OOP principles, MySQL database, and role-based access control to manage users and contacts. It was developed as part of the CMPE343 Advanced Java Programming course at Kadir Has University.

Project Overview

The system provides:

A login authentication system Role-specific operations for: Tester Junior Developer Senior Developer Manager A full MySQL database integration CRUD operations on users and contacts Single-field and multi-field search operations Sorting, validation, and dynamic menus Turkish character support ASCII startup & shutdown animations Javadoc documentation The application runs entirely in the terminal, interacting with MySQL using JDBC.

🧩 Features 🔐 Authentication & Role-Based Access

Each user logs in with: username password (hashed in DB)

Their role determines which menu they see and what operations they can perform.

👥 User Roles & Permissions

Role Features Tester Change password, list/search/sort contacts, logout Junior Developer Tester features + update contacts Senior Developer Junior Developer features + add/delete contacts Manager All above + manage users, view contact statistics

📁 Database Structure Users Table

Contains fields: user_id username password_hash name surname role created_at

Contacts Table Contains fields: contact_id first_name middle_name last_name nickname phone_primary phone_secondary email linkedin_url birth_date created_at updated_at

At least 50 contacts must be manually inserted for testing.

🏗 Technologies Used Java 17+ MySQL 8+ JDBC Driver OOP Concepts Inheritance Polymorphism Encapsulation Abstraction GitHub for collaboration Javadoc for documentation

📦 Project Structure (src/)

/database ConnectionDB.java UsersOperations.java ContactsOperations.java Main.java

/models User.java Contact.java

/utils Validator.java HashUtil.java MenuRenderer.java

🧪 Test Accounts

The system includes these predefined users: Tester → username: tt, password: tt Junior Developer → jd / jd Senior Developer → sd / sd Manager → man / man

📘 Documentation

Generate Javadoc: javadoc -d doc -sourcepath src -subpackages database:models:utils

🎥 Demo Video A full demo video is included showing:

🤝 Contributors Ahmet Furkan Gökbulut Elif Gülüm Yağmur Güzeler Zeynep Duygu Ortancıl
