# Experiment 3.1.1 / 3.1.2 — Kafka Reliability

A small Spring Boot + Apache Kafka lab demonstrating:
- retry handling with exponential backoff,
- a Dead Letter Topic (DLT/DLQ) after retries,
- idempotency using event IDs.

## Requirements
- JDK 17+
- Maven 3.8+ (or use your IDE's Maven support)
- Docker Desktop running

## 1. Start Kafka
From this project folder, run:

```bash
docker compose up -d
docker compose ps
```

Wait until the Kafka container is running.

## 2. Run the Spring Boot app
In another terminal, from the project folder:

```bash
mvn spring-boot:run
```

The app runs at `http://localhost:8080`.

## 3. Test a successful event (Windows PowerShell)
```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/posts -ContentType 'application/json' -Body '{"eventId":"post-001","content":"Hello Kafka","scheduledAt":"2026-10-28T06:00:00Z"}'
```
Check the application terminal for `SUCCESS`.

## 4. Test retries and DLQ
The content marker `[FAIL]` intentionally throws an exception. It will be retried with exponential backoff and then sent to `scheduled-posts-DLT`.

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/posts -ContentType 'application/json' -Body '{"eventId":"post-fail-001","content":"[FAIL] simulate a permanent error","scheduledAt":"2026-10-28T06:00:00Z"}'
```
Look in the app logs for retry processing and `DLQ RECEIVED`.

## 5. Test idempotency
Send the same successful event twice with the same `eventId`:

```powershell
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/posts -ContentType 'application/json' -Body '{"eventId":"post-duplicate-001","content":"Duplicate test","scheduledAt":"2026-10-28T06:00:00Z"}'
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/posts -ContentType 'application/json' -Body '{"eventId":"post-duplicate-001","content":"Duplicate test","scheduledAt":"2026-10-28T06:00:00Z"}'
```

The consumer should log `SUCCESS` once and `DUPLICATE SKIPPED` for the duplicate (Kafka timing can affect ordering).

## 6. Push to GitHub
1. Create a new **empty** repository on GitHub (don't add README or .gitignore there).
2. Extract this ZIP.
3. Open a terminal in the extracted `kafka-reliability-lab` folder and run:

```bash
git init
git add .
git commit -m "Implement Kafka reliability patterns"
git branch -M main
git remote add origin https://github.com/YOUR-USERNAME/YOUR-REPOSITORY.git
git push -u origin main
```

Replace the URL with your own repository URL. GitHub may ask you to sign in through your browser.

## Important notes for your lab report
- Retry: 4 total attempts (initial attempt + retries as configured by Spring Kafka) with backoff delay increasing approximately 1s, 2s, 4s.
- DLT: events that keep failing are routed to `scheduled-posts-DLT`.
- Idempotency: repeated event IDs are skipped after the first successful processing.
- The idempotency store here is in-memory to keep the experiment easy to run. It resets when the app restarts. In production, use a persistent database table with a unique constraint on `event_id`.
- The endpoint publishes an event immediately; `scheduledAt` is metadata for the lab, not a scheduler that waits until that time.
- This is a teaching/demo implementation. Verify it in your environment before submission.

## Stop Kafka
```bash
docker compose down
```
