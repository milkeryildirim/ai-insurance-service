# AI Insurance Service

## 🏢 Project Overview

This project demonstrates how Spring AI works in practice. The goal of the project is to show how AI
capabilities can be added to a regular microservice. To run this test project correctly, you must
first run the application from the https://github.com/milkeryildirim/dummy-insurance-service
repository on port 8080. Then, the commands you give through the chat panel will be forwarded to the
dummy-insurance-service by this AI-enabled application.

AI Insurance Service is an intelligent customer service assistant built with Spring Boot and
Google's Vertex AI Gemini technologies. The service provides automated customer support for
insurance operations, allowing customers to interact through natural language for policy inquiries,
claim processing, and customer information access.

## ✨ Features

### 🤖 AI-Powered Customer Service

- **Natural Language Processing**: Powered by Google Vertex AI Gemini 2.5 Pro
- **Multi-language Support**: Automatically detects and responds in any language the customer uses
- **Conversation Memory**: Maintains context throughout the conversation with in-memory chat history
- **Human Handoff**: Seamlessly escalates complex issues to human operators when needed
- **Identity Verification**: Automatic customer identity verification system for security
- **Date Awareness**: Current date awareness for calculating policy cancellation rights and
  deadlines

### 🔧 Available AI Functions

#### 👤 Customer Management (7 Functions)

- **Customer Search**: Find customers by policy number (`getCustomerByPolicyNumber`)
- **Customer Details**: Retrieve customer details by ID (`getCustomerById`)
- **Customer Listing**: Search customers by name or list all customers (`getAllCustomers`)
- **Customer Creation**: Create new customer records (`createCustomer`)
- **Customer Updates**: Update existing customer information (`updateCustomer`)
- **Customer Deletion**: Remove customer records from system (`deleteCustomer`)
- **Customer Policies**: View all policies belonging to a customer (`getPoliciesByCustomerId`)

#### 📋 Policy Management (5 Functions)

- **Policy Details**: Get policy information by ID (`getPolicyById`) or policy number (
  `getPolicyByPolicyNumber`)
- **Policy Listing**: List all policies (`getAllPolicies`)
- **Policy Creation**: Create new policy records (`createPolicy`)
- **Policy Updates**: Update existing policy information (`updatePolicy`)
- **Policy Conditions**: View and update system-wide policy terms and conditions (
  `getPolicyConditions`, `updatePolicyConditions`)

#### 🚗🏠💊 Comprehensive Claim Management (21 Functions)

**Auto Claims (7 Functions):**

- Create new auto claim records (`createAutoClaim`)
- View and update claim details (`getAutoClaimById`, `updateAutoClaim`)
- Assign adjusters to claims (`assignAdjusterToAutoClaim`)
- Filter and list claims (`getAllAutoClaims`, `getAutoClaimsByPolicyId`)
- Delete claim records (`deleteAutoClaim`)

**Home Claims (7 Functions):**

- Create home/property damage claims (`createHomeClaim`)
- Track damage types and affected items (`getHomeClaimById`, `updateHomeClaim`)
- Assign adjusters for property assessment (`assignAdjusterToHomeClaim`)
- Manage claim listings (`getAllHomeClaims`, `getHomeClaimsByPolicyId`)
- Delete home claims (`deleteHomeClaim`)

**Health Claims (7 Functions):**

- Create medical expense claim requests (`createHealthClaim`)
- Track medical providers and procedure codes (`getHealthClaimById`, `updateHealthClaim`)
- Manage reimbursement processes (`getAllHealthClaims`, `getHealthClaimsByPolicyId`)
- Assign medical adjusters (`assignAdjusterToHealthClaim`)
- Delete health claims (`deleteHealthClaim`)

#### 🆘 Human Operator Handoff (1 Function)

- Escalate unresolved issues to human agents (`informHumanOperator`)
- Log requests requiring manual intervention

### 🌐 Web Interface

- Simple and intuitive chat interface
- Bootstrap 5.3.3-powered responsive design
- Real-time messaging with loading indicators
- Clean conversation history display

## 🏗️ Architecture

### Technology Stack

- **Backend**: Spring Boot 3.5.4
- **AI Engine**: Spring AI 1.0.1 with Vertex AI Gemini 2.5 Pro
- **Java Version**: 21
- **Build Tool**: Maven
- **API Communication**: OpenFeign with Spring Cloud 2025.0.0
- **Testing**: JUnit 5, Mockito, AssertJ
- **Frontend**: HTML, CSS, Bootstrap 5.3.3
- **Validation**: Spring Boot Starter Validation

### Project Structure

```
ai-insurance-service/
├── application/                          # Main application module
│   ├── src/main/java/
│   │   └── tech/yildirim/aiinsurance/
│   │       ├── AiInsuranceApplication.java
│   │       ├── ai/functions/             # AI function definitions
│   │       │   ├── ClaimFunctions.java   # Claim management (21 functions)
│   │       │   ├── CustomerFunctions.java # Customer management (7 functions)
│   │       │   ├── PolicyFunctions.java  # Policy management (5 functions)
│   │       │   ├── HandoffFunctions.java # Human operator handoff (1 function)
│   │       │   └── Functions.java        # Function constants (34 total functions)
│   │       ├── controller/               # REST controllers
│   │       │   └── ChatController.java
│   │       └── service/                  # Business logic
│   │           └── ChatService.java
│   ├── src/main/resources/
│   │   ├── application.yaml              # Configuration with comprehensive AI prompt
│   │   ├── openapi-templates/            # OpenAPI templates
│   │   └── static/index.html             # Web interface
│   └── src/test/                         # Unit tests
│       └── java/tech/yildirim/aiinsurance/ai/functions/
│           ├── ClaimFunctionsTest.java   # Claim function tests
│           ├── CustomerFunctionsTest.java
│           ├── CustomerFunctionsIntegrationTest.java
│           ├── PolicyFunctionsTest.java
│           └── ...
└── pom.xml                              # Parent POM
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- Google Cloud Platform account with Vertex AI enabled
- **Dummy Insurance API service must be running on `localhost:8080`**

### Environment Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ai-insurance-service
   ```

2. **Start the Dummy Insurance Service**
   ```bash
   # First clone and run the dummy-insurance-service repository
   git clone https://github.com/milkeryildirim/dummy-insurance-service
   cd dummy-insurance-service
   mvn spring-boot:run  # Will run on port 8080
   ```

3. **Set up Google Cloud credentials**
   ```bash
   export GCP_PROJECT_ID=your-gcp-project-id
   export GCP_VERTEX_API_KEY=your-vertex-ai-api-key
   ```

4. **Install dependencies**
   ```bash
   mvn clean install
   ```

5. **Run the application**
   ```bash
   cd application
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8081`.

### Configuration

Key configuration properties in `application.yaml`:

```yaml
server:
  port: 8081

spring:
  ai:
    vertex:
      ai:
        gemini:
          project-id: ${GCP_PROJECT_ID}
          api-key: ${GCP_VERTEX_API_KEY}
          location: us-central1
          chat:
            options:
              model: gemini-2.5-pro

insurance:
  service:
    base-url: http://localhost:8080

config:
  default-prompt: |
    # Comprehensive AI system prompt with:
    # - Date awareness (August 30, 2025)
    # - Identity verification protocols
    # - Multi-language support
    # - Function usage guidelines
    # - Security protocols
```

## 📡 API Endpoints

### Chat API

- **POST** `/api/chat`
    - **Request Body**: `{"message": "Your question here"}`
    - **Response**: `{"response": "AI assistant response"}`

### Web Interface

- **GET** `/` - Access the web-based chat interface

## 🧪 Testing

The project includes comprehensive unit tests for all AI functions:

```bash
mvn test
```

Test coverage includes:

- **ClaimFunctionsTest**: Comprehensive claim function validation
- **CustomerFunctionsTest**: Customer function validations
- **CustomerFunctionsIntegrationTest**: Integration test scenarios
- **PolicyFunctionsTest**: Policy lookup operation tests
- Error handling scenarios
- AI function integration tests

## 🔧 Development

### Adding New AI Functions

1. Create a new function class in `ai.functions` package
2. Define request/response records
3. Implement the function with `@Bean` and `@Description` annotations
4. Add the function name to `Functions.ALL_FUNCTIONS`
5. Write corresponding unit tests

Example:

```java

@Bean("myNewFunction")
@Description("Description of what this function does")
public Function<MyRequest, MyResponse> myNewFunction() {
  return request -> {
    // Implementation
  };
}
```

### Customizing AI Behavior

Modify the default-prompt section in `application.yaml` to adjust:

- AI personality and tone
- Response formats
- Conversation flow rules
- Language handling
- Identity verification process
- Date awareness

## 🎯 AI Capabilities Details

### Smart Identity Verification

- Customer identification through policy numbers
- Random security questions (address, birth date, city)
- Security denial for incorrect information
- Multi-attempt protection

### Date-Aware Operations

- Current date: August 30, 2025
- Policy cancellation rights calculation
- Claim reporting deadlines
- Policy renewal scheduling

### Multi-Language Support

- Automatic language detection for any language
- Language consistency throughout conversation
- Global language support without restrictions

## 📋 Dependencies

### Core Dependencies

- `spring-boot-starter-web` - Web framework
- `spring-ai-starter-model-vertex-ai-gemini` - AI integration
- `spring-cloud-starter-openfeign` - HTTP client
- `spring-boot-starter-validation` - Input validation
- `dummy-insurance-api-contract` - Insurance API contract

### Development Dependencies

- `lombok` - Code generation
- `spring-boot-starter-test` - Testing framework
- `assertj-core` - Fluent assertions

## 🚀 Deployment

### Docker Deployment

```dockerfile
FROM openjdk:21-jre-slim
COPY application/target/ai-insurance-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Environment Variables

- `GCP_PROJECT_ID` - Google Cloud Project ID
- `GCP_VERTEX_API_KEY` - Vertex AI API Key

## 🤖 Example Usage Scenarios

### Claim Reporting

```
Customer: "Hello, I had a car accident today and want to create a claim record."
AI: "Hello! I'll be happy to help you. For security purposes, we need to verify your identity first..."
```

### Policy Inquiry

```
Customer: "Can I get details about my policy POL-12345?"
AI: "Of course! For identity verification, could you please confirm your address on file?"
```

### Policy Cancellation

```
Customer: "I want to cancel my policy."
AI: "I'll help you with the policy cancellation. Let me check the time between your policy start date and today's date to determine your cancellation rights..."
```

## 📞 Support and Contribution

This project serves as an example of how Spring AI can be used in the insurance industry. The
experiences during the development process and AI integration techniques will be detailed in a blog
post.

---

**Note**: This project is for testing purposes and should be thoroughly reviewed for security,
performance, and scalability before use in a production environment.
