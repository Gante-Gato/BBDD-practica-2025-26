  Local SQL Query Tool

A small student project that allows you to interact with a local MySQL database directly from the terminal. You can execute queries, retrieve results, and export them in a convenient format for further use.

Features

Connects to a local MySQL server on localhost:3306.

Execute SQL queries and view results in the terminal.

Export query results to .csv or .xml files.

Option to create a local MySQL user with root-level permissions.

Fully terminal-based, no graphical interface required.

Requirements

MySQL server running locally.

Terminal access.

Appropriate credentials to connect to the local database.

Setup

Clone or download the project and ensure your local MySQL server is running. Configure your connection details as needed in the project’s configuration file.

Usage

The tool is designed for simple, straightforward use from the terminal. You can:

Connect to the local MySQL server.

Execute queries and instantly see results.

Export the results to CSV or XML for further analysis or reporting.

Manage users by creating a new local MySQL user with full privileges if needed.

Notes

Only local connections are supported; this project is not intended for remote databases.

Exports are intended for queries that retrieve data (SELECT statements).

Being a student project, all operations are done via terminal; there is no graphical interface.

Project Structure

The project is organized to separate core logic, configuration, and output files for clarity and ease of use.
