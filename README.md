<p align="center">
  <img src="https://img.shields.io/badge/🚀-JobQuest-blueviolet?style=for-the-badge&logoColor=white" alt="JobQuest" height="60"/>
</p>

<h1 align="center">🎯 JobQuest</h1>

<p align="center">
  <i>Your quest for the perfect job starts here.</i><br/>
  <b>Track · Organize · Conquer</b> — The ultimate internship & job application tracker for ambitious students.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.2.3-6DB33F?style=flat-square&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/SQLite-07405E?style=flat-square&logo=sqlite&logoColor=white" />
  <img src="https://img.shields.io/badge/Thymeleaf-005F0F?style=flat-square&logo=thymeleaf&logoColor=white" />
  <img src="https://img.shields.io/badge/Bootstrap-5.3-7952B3?style=flat-square&logo=bootstrap&logoColor=white" />
  <img src="https://img.shields.io/badge/License-MIT-yellow?style=flat-square" />
</p>

---

## 🧭 The Problem

> You've applied to 50+ jobs. Some are in "Applied" limbo, two have interviews next week, and you *think* you got rejected from that one company... but which one? 🤯

Sound familiar? **JobQuest** eliminates the chaos. No more spreadsheets from hell. No more sticky notes falling off your monitor. Just a clean, powerful, and *beautiful* tracker that works as hard as you do.

---

## ✨ Features at a Glance

| Feature | Description |
|:--|:--|
| 📊 **Smart Dashboard** | Bird's-eye view of your entire job hunt — total apps, interviews, offers, rejections — all in real time |
| 📋 **Application Manager** | Full CRUD with filtering by status & company name. Never lose track of an application again |
| 🗂️ **Kanban Board** | Drag-and-drop your applications across stages: `Applied → OA → Interviewing → Offer / Rejected` |
| 📈 **Analytics Engine** | Visual charts showing monthly trends, status distribution, and your application velocity |
| 🔔 **Interview Alerts** | Instant visibility into upcoming interviews so you never miss a round |
| 📎 **File Uploads** | Attach resumes & cover letters directly to each application (up to 10 MB) |
| 🎨 **Modern UI** | Glassmorphism-inspired design with Inter font, Bootstrap Icons, and smooth animations |
| ⚡ **Zero Config DB** | SQLite — no database server required. Just run and go |
| 🔄 **Hot Reload** | Spring Boot DevTools for instant feedback during development |

---

## 🖼️ Application Flow

```
                    ┌─────────────────────────────┐
                    │        📊 DASHBOARD         │
                    │   Stats · Alerts · Recent   │
                    └────────────┬────────────────┘
                                 │
              ┌──────────────────┼───────────────────┐
              ▼                  ▼                   ▼
     ┌────────────────┐ ┌───────────────┐  ┌─────────────────┐
     │📋 APPLICATIONS│  │ 🗂️ KANBAN    │  │ 📈 ANALYTICS   │
     │ List & Filter  │ │ Drag & Drop   │  │ Charts & Trends │
     └───────┬────────┘ └───────────────┘  └─────────────────┘
             │
    ┌────────┴────────┐
    ▼                 ▼
┌──────────┐   ┌──────────┐
│ ➕ ADD   │   │ ✏️ EDIT  │
│ New App  │   │ + Upload │
└──────────┘   └──────────┘
```

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+** — [Download](https://adoptium.net/)
- **Maven 3.8+** — [Download](https://maven.apache.org/download.cgi)

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/Shailendra1122/JobQuest.git
cd JobQuest
```

### 2️⃣ Build & Run

```bash
# Build the project
mvn clean install

# Launch the application
mvn spring-boot:run
```

### 3️⃣ Open in Browser

```
🌐 [https://job-quest-eosin.vercel.app/](https://jobquest-gqlq.onrender.com)
```

> 💡 **That's it!** No database setup, no environment variables, no Docker. SQLite creates the `jobquest.db` file automatically on first run, pre-loaded with sample data.

---

## 🏗️ Project Architecture

```
JobQuest/
├── src/main/java/com/jobquest/
│   ├── 🚀 JobQuestApplication.java      # Entry point
│   ├── config/
│   │   └── ⚙️ WebConfig.java            # CORS & MVC config
│   ├── controller/
│   │   ├── 🌐 JobApplicationController.java    # Thymeleaf MVC routes
│   │   └── 🔌 JobApplicationApiController.java # REST API endpoints
│   ├── model/
│   │   └── 📦 JobApplication.java        # JPA Entity
│   ├── repository/
│   │   └── 🗃️ JobApplicationRepository.java   # Spring Data JPA
│   └── service/
│       └── 🧠 JobApplicationService.java # Business logic
│
├── src/main/resources/
│   ├── application.properties            # App configuration
│   ├── data.sql                          # Sample seed data
│   ├── static/
│   │   ├── css/style.css                 # Custom styles
│   │   └── js/
│   │       ├── app.js                    # Core UI logic
│   │       ├── kanban.js                 # Kanban drag-and-drop
│   │       └── analytics.js             # Chart rendering
│   └── templates/
│       ├── dashboard.html                # Dashboard view
│       ├── applications.html             # App list view
│       ├── form.html                     # Add/Edit form
│       ├── kanban.html                   # Kanban board
│       ├── analytics.html                # Analytics page
│       └── fragments/layout.html         # Shared layout
│
└── pom.xml                              # Maven dependencies
```

---

## 🎮 Application Status Pipeline

Every application moves through a clear lifecycle:

```
 ┌──────────┐    ┌────────────────────┐    ┌───────────────┐    ┌─────────┐
 │ 📝       │    │ 💻                │    │ 🎤            │    │ 🎉     │
 │ Applied  │───▶│ Online Assessment  │───▶│ Interviewing │───▶│ Offer! │
 │          │    │                    │    │               │    │         │
 └──────────┘    └────────────────────┘    └───────┬───────┘    └─────────┘
                                                   │
                                                   ▼
                                            ┌──────────┐
                                            │ ❌       │
                                            │ Rejected │
                                            └──────────┘
```

---

## 🔌 REST API

JobQuest also exposes a **REST API** for programmatic access:

| Method | Endpoint | Description |
|:--|:--|:--|
| `GET` | `/api/applications` | List all applications |
| `GET` | `/api/applications/{id}` | Get application by ID |
| `POST` | `/api/applications` | Create new application |
| `PUT` | `/api/applications/{id}` | Update application |
| `DELETE` | `/api/applications/{id}` | Delete application |
| `PATCH` | `/api/applications/{id}/status` | Update status (Kanban) |

---

## 🛠️ Tech Stack Deep Dive

| Layer | Technology | Why? |
|:--|:--|:--|
| **Backend** | Spring Boot 3.2.3 | Production-grade framework with auto-configuration |
| **Language** | Java 17 | Modern LTS with records, sealed classes, pattern matching |
| **Database** | SQLite + Hibernate | Zero-config, portable, file-based database |
| **Template** | Thymeleaf | Server-side rendering with natural HTML templates |
| **Frontend** | Bootstrap 5.3 | Responsive, modern UI components out of the box |
| **Icons** | Bootstrap Icons | 1,800+ beautiful SVG icons |
| **Typography** | Google Fonts (Inter) | Clean, professional typeface |
| **Build** | Maven | Dependency management & build automation |
| **Dev Tools** | Spring DevTools | Live reload during development |

---

## 🎨 Design Highlights

- 🌑 **Dark glassmorphism theme** — Sleek, modern, easy on the eyes
- ✨ **Smooth animations** — Cards, buttons, and transitions feel alive
- 📱 **Fully responsive** — Desktop, tablet, and mobile ready
- 🎯 **Intuitive navigation** — Dashboard → Apps → Kanban → Analytics in one click
- 🔔 **Toast notifications** — Instant feedback on every action

---

## 🤝 Contributing

Contributions are welcome! Here's how to get involved:

1. 🍴 **Fork** the repository
2. 🌿 **Create** a feature branch: `git checkout -b feature/amazing-feature`
3. 💻 **Commit** your changes: `git commit -m 'Add some amazing feature'`
4. 🚀 **Push** to the branch: `git push origin feature/amazing-feature`
5. 📬 **Open** a Pull Request

---

## 📜 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

<p align="center">
  <b>Built with ❤️ by <a href="https://github.com/Shailendra1122">Shailendra</a></b>
</p>

<p align="center">
  <i>⭐ Star this repo if JobQuest helps you land your dream job! ⭐</i>
</p>
