# Parking Control Application - Spring Boot 3 + PostgreSQL


## Table of Contents:

🎯 [Objective](#-objective)  
🏃 [Running the project](#-running-the-project)  
📄 [Scripts](#-scripts)  
🔍 [Visualizing Data](#-visualizing-data)      
🚧 [Troubleshooting](#-troubleshooting)


---


## 🎯 Objective

The mainly objective of this application is to create an API of a Parking Control focusing on parking spot resource. To achieve that, I use some Spring Environment tools like:
- 
- Spring MVC
- Spring Data
- Spring Validation


## 🏃 Running the project

You should have a **Docker** environment with support to **Docker Compose V2**.

> ⚠️ _This project uses bash scripts to make some commands easier to run and was tested only on a Linux machine. If you are using Windows, I highly recommend you running this project inside a WSL2 distro, or using Git Bash as your terminal._

Open your terminal in the root folder and type:

```bash
sh ./scripts/run.sh
```

This script will make sure to build your images and in subsequent runs, it will skip the installation step and directly start all containers.

To stop running containers, just type:

```bash
./scripts/stop.sh
```

and all your containers will be dropped and volumes will be removed.


## 📄 Scripts

Beyond `run.sh` and `stop.sh`, we have other helper scripts:

- `build.sh`: Rebuilds the images in case you changed something in the Dockerfiles
- `run-db.sh`: Run only database specific containers, when you want to run the application without Docker


## 🔍 Visualizing Data

PostgreSQL's service are not exposed at any port to the host machine for simulating an isolated network environment, so you cannot connect directly to them. Please, use the interface available through **Adminer**:

- _Adminer_ available at [`http://localhost:8080/`](http://localhost:8080/)
    - **System**: `PostgreSQL`
    - **Server**: `db-postgresql:5432`
    - **User**: `admin`
    - **Password**: `admin123`


## 🚧 Troubleshooting

- Make sure you have these ports available before running the projects:
    - **`3000`**: React development server
    - **`8000`**: Used by Spring Boot API
    - **`8080`**: Adminer
- Make sure your Docker daemon is running!
- Make sure you are using a newer version of Docker that supports Docker Compose V2! **This project does not use `docker-compose`** (a.k.a. V1) because this version will no longer be supported from the end of June 2023.
- If you are somehow receiving `Permission denied` when trying to run any scripts, run
  ```sh
  chmod +x ./*.sh && chmod +x ./docker/*.sh
  ```
  to make sure your terminal can execute utility scripts and Spring Boot's container can execute the entrypoint script.
- If you run the `run.sh` script and you received a not found file error, you probably need to build the backend application before running this script.
  ```sh
  mvn clean package -f ./backend
  ```
  this will generate the `.jar` file and now you can run the script again.
