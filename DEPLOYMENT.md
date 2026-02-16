# RevHire Deployment Guide

## Prerequisites
- Docker and Docker Compose installed
- Git installed
- JDK 17+ (for local development)
- Maven 3.6+ (for local development)

## Deployment Options

### Option 1: Docker Compose (Recommended)

#### Step 1: Clone the Repository
```bash
git clone <your-repo-url>
cd RevHire
```

#### Step 2: Update Environment Variables
Create a `.env` file in the project root:

```env
# Database Configuration
DB_URL=jdbc:mysql://db:3306/revhire_db
DB_USER=root
DB_PASS=rootpassword

# Application Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
```

#### Step 3: Build and Run with Docker Compose
```bash
# Build and start all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

The application will be available at `http://localhost:8080`

### Option 2: Manual Deployment

#### Step 1: Set Up MySQL Database
```bash
mysql -u root -p
CREATE DATABASE revhire_db;
```

#### Step 2: Update application.properties
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/revhire_db
spring.datasource.username=root
spring.datasource.password=your_password
```

#### Step 3: Build the Application
```bash
mvn clean package -DskipTests
```

#### Step 4: Run the Application
```bash
java -jar target/revhire-web-0.0.1-SNAPSHOT.jar
```

### Option 3: AWS EC2 Deployment

#### Step 1: Launch EC2 Instance
- Instance Type: t2.medium or higher
- OS: Ubuntu 22.04 LTS
- Security Group: Allow ports 22 (SSH), 80 (HTTP), 443 (HTTPS), 8080 (App)

#### Step 2: Install Dependencies
```bash
# Connect to EC2
ssh -i your-key.pem ubuntu@your-ec2-ip

# Update system
sudo apt update && sudo apt upgrade -y

# Install Docker
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker $USER

# Install Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
```

#### Step 3: Clone and Deploy
```bash
git clone <your-repo-url>
cd RevHire

# Update environment variables
nano .env

# Deploy
docker-compose up -d
```

#### Step 4: Configure Nginx (Optional - for production)
```bash
sudo apt install nginx -y

# Create Nginx config
sudo nano /etc/nginx/sites-available/revhire
```

Add this configuration:
```nginx
server {
    listen 80;
    server_name your-domain.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

Enable the site:
```bash
sudo ln -s /etc/nginx/sites-available/revhire /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

## Production Checklist

### Security
- [ ] Change default database password
- [ ] Update `application-prod.properties` with production database URL
- [ ] Configure HTTPS with SSL certificate (Let's Encrypt)
- [ ] Enable CSRF protection in Spring Security
- [ ] Set up rate limiting
- [ ] Configure secure session management

### Database
- [ ] Run database migrations if needed
- [ ] Set up automated backups
- [ ] Configure database connection poolproperly (HikariCP settings)
- [ ] Monitor database performance

### Monitoring & Logging
- [ ] Set up application logs aggregation
- [ ] Configure Spring Boot Actuator endpoints
- [ ] Set up health checks
- [ ] Configure alerts for errors

### Performance
- [ ] Enable Thymeleaf template caching (`spring.thymeleaf.cache=true`)
- [ ] Configure proper JVM memory settings
- [ ] Enable GZip compression
- [ ] Set up CDN for static assets (optional)

## Updating the Application

```bash
# Pull latest changes
git pull origin main

# Rebuild and restart
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

## Troubleshooting

### Application won't start
```bash
# Check logs
docker-compose logs app

# Check database connection
docker-compose exec db mysql -u root -p revhire_db
```

### Database connection issues
- Verify database credentials in `.env` file
- Check if MySQL container is running: `docker ps`
- Check network connectivity: `docker network ls`

### Port already in use
```bash
# Find process using port 8080
lsof -i :8080  # macOS/Linux
netstat -ano | findstr :8080  # Windows

# Kill the process or change the port in docker-compose.yml
```

## Backup and Restore

### Backup Database
```bash
docker-compose exec db mysqldump -u root -p revhire_db > backup_$(date +%Y%m%d).sql
```

### Restore Database
```bash
docker-compose exec -T db mysql -u root -p revhire_db < backup_20260214.sql
```

## Scaling (Future Enhancement)

Consider these options when you need to scale:

1. **Horizontal Scaling**: Run multiple application instances behind a load balancer
2. **Database Read Replicas**: For read-heavy workloads
3. **Caching**: Implement Redis for session storage and caching
4. **Message Queue**: Add RabbitMQ for async job processing

## Support

For issues and questions, please open a GitHub issue or contact the development team.
