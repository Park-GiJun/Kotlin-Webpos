# Kotlin WebPOS - 개발 가이드

## 🚀 개발 환경 설정

### 필수 요구사항
- **JDK 21** 이상
- **Docker & Docker Compose**
- **Kotlin 1.9.25**
- **Spring Boot 3.5.5**
- **Gradle 8.5**

### 로컬 개발 환경 구성
```bash
# 1. 프로젝트 클론
git clone https://github.com/Park-GiJun/Kotlin-Webpos.git
cd Kotlin-Webpos

# 2. 인프라 서비스 실행
cd docker
docker-compose -f docker-compose-infrastructure.yml up -d

# 3. 각 서비스별로 실행
# Terminal 1: Main Server
cd main-server
./gradlew bootRun

# Terminal 2: POS Server
cd pos-server
./gradlew bootRun
```

### IDE 설정 (IntelliJ IDEA)
```
1. Kotlin Plugin 활성화
2. Spring Boot Plugin 설치
3. Database Navigator Plugin 설치
4. Docker Integration Plugin 설치
```

---

## 📁 프로젝트 구조 상세

### 전체 디렉토리 구조
```
kotlin-webpos/
├── docs/                      # 📚 프로젝트 문서
│   ├── ARCHITECTURE.md        # 아키텍처 문서
│   ├── DEVELOPMENT_GUIDE.md   # 개발 가이드
│   └── EVENT_CATALOG.md       # 이벤트 카탈로그
├── docker/                    # 🐳 Docker 설정
│   ├── docker-compose.yml     # 전체 MSA 구성
│   ├── docker-compose-infrastructure.yml  # 인프라만
│   └── init/                  # DB 초기화 스크립트
├── main-server/               # 🏢 Main Server (HQ/Store/Product 관리)
│   ├── src/main/kotlin/com/gijun/mainserver/
│   │   ├── application/       # 🔧 Application Layer
│   │   ├── domain/           # 💼 Domain Layer
│   │   └── infrastructure/   # 🔌 Infrastructure Layer
│   ├── Dockerfile
│   └── build.gradle
├── pos-server/               # 🛒 POS Server (POS/Sales 관리)
│   ├── src/main/kotlin/com/gijun/posserver/
│   │   ├── application/      # 🔧 Application Layer  
│   │   ├── domain/          # 💼 Domain Layer
│   │   └── infrastructure/  # 🔌 Infrastructure Layer
│   ├── Dockerfile
│   └── build.gradle
├── build.gradle              # 🏗️ 루트 빌드 설정
├── settings.gradle           # ⚙️ 프로젝트 설정
└── .gitignore               # 📝 Git 무시 파일
```

### 각 서비스별 레이어 구조
```
{service}/src/main/kotlin/com/gijun/{service}/
├── application/              # 🔧 애플리케이션 레이어
│   ├── dto/                 # 데이터 전송 객체
│   │   ├── command/        # 명령(쓰기) 요청
│   │   ├── query/          # 조회(읽기) 요청
│   │   └── result/         # 응답 결과
│   ├── handler/            # 요청 처리기
│   │   ├── command/        # 명령 처리기
│   │   └── query/          # 조회 처리기
│   ├── mapper/             # DTO ↔ Domain 매핑
│   └── port/               # 포트 (인터페이스)
│       ├── in/             # 인바운드 포트 (유즈케이스)
│       └── out/            # 아웃바운드 포트 (리포지토리)
├── domain/                   # 💼 도메인 레이어
│   ├── common/             # 공통 Value Objects
│   │   ├── vo/            # Email, Money, PhoneNumber 등
│   │   └── exception/     # 도메인 예외
│   └── {domain}/          # 비즈니스 도메인
│       ├── model/         # 도메인 엔티티
│       └── vo/           # 도메인별 Value Objects
└── infrastructure/           # 🔌 인프라스트럭처 레이어
    ├── config/             # 설정 클래스
    └── adapter/
        ├── in/web/         # REST API 컨트롤러
        │   ├── dto/       # Request/Response DTO
        │   └── mapper/    # Web ↔ Application 매핑
        └── out/            # 외부 시스템 어댑터
            ├── persistence/ # JPA Repository
            ├── event/      # 이벤트 발행/구독
            └── external/   # 외부 API 호출
```

---

## 🎯 CQRS 패턴 구현 가이드

### 1. Command 생성
```kotlin
// 📝 Command 정의
data class CreateHqCommand(
    val name: String,
    val representative: String,
    val address: String,
    val email: String?,
    val phoneNumber: String?
) {
    init {
        require(name.isNotBlank()) { "본사명은 필수입니다" }
        require(representative.isNotBlank()) { "대표자명은 필수입니다" }
        require(address.isNotBlank()) { "주소는 필수입니다" }
    }
}

// 📤 Command Result 정의
data class CreateHqResult(
    val hqId: Long,
    val name: String,
    val createdAt: Instant
) {
    companion object {
        fun from(hq: Hq): CreateHqResult {
            return CreateHqResult(
                hqId = hq.id?.value ?: throw IllegalStateException("ID가 없습니다"),
                name = hq.name,
                createdAt = hq.createdAt
            )
        }
    }
}
```

### 2. Query 생성
```kotlin
// 📖 Query 정의
data class GetHqByIdQuery(val hqId: Long)

data class GetAllHqQuery(
    val page: Int = 0,
    val size: Int = 20,
    val sortBy: String = "createdAt",
    val sortDirection: String = "DESC"
)

// 📥 Query Result 정의
data class HqResult(
    val hqId: Long,
    val name: String,
    val representative: String,
    val address: String,
    val email: String?,
    val phoneNumber: String?,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(hq: Hq): HqResult {
            return HqResult(
                hqId = hq.id?.value ?: 0L,
                name = hq.name,
                representative = hq.representative,
                address = hq.address.fullAddress,
                email = hq.email?.value,
                phoneNumber = hq.phoneNumber?.value,
                createdAt = hq.createdAt,
                updatedAt = hq.updatedAt
            )
        }
    }
}
```

### 3. Handler 구현
```kotlin
// 🔧 Command Handler
@Component
@Transactional
class HqCommandHandler(
    private val hqCommandRepository: HqCommandRepository,
    private val hqEventPublisher: HqEventPublisher
) : CreateHqUseCase, UpdateHqUseCase, DeleteHqUseCase {

    override fun handle(command: CreateHqCommand): CreateHqResult {
        // 1. 도메인 엔티티 생성
        val hq = Hq.create(
            name = command.name,
            representative = command.representative,
            address = Address(command.address),
            email = command.email?.let { Email(it) },
            phoneNumber = command.phoneNumber?.let { PhoneNumber(it) }
        )
        
        // 2. 저장
        val savedHq = hqCommandRepository.save(hq)
        
        // 3. 도메인 이벤트 발행
        hqEventPublisher.publishHqCreated(
            HqCreatedEvent.from(savedHq)
        )
        
        return CreateHqResult.from(savedHq)
    }
}

// 🔍 Query Handler
@Component
@Transactional(readOnly = true)
class HqQueryHandler(
    private val hqQueryRepository: HqQueryRepository
) : GetHqUseCase {

    override fun handle(query: GetHqByIdQuery): HqResult? {
        return hqQueryRepository.findById(query.hqId)
            ?.let { HqResult.from(it) }
    }
    
    override fun handle(query: GetAllHqQuery): Page<HqResult> {
        return hqQueryRepository.findAll(
            PageRequest.of(
                query.page,
                query.size,
                Sort.by(
                    Sort.Direction.fromString(query.sortDirection),
                    query.sortBy
                )
            )
        ).map { HqResult.from(it) }
    }
}
```

---

## 🔄 이벤트 기반 통신 구현

### 1. 도메인 이벤트 정의
```kotlin
// 🎯 Base Domain Event
abstract class DomainEvent(
    open val eventId: String = UUID.randomUUID().toString(),
    open val occurredAt: Instant = Instant.now(),
    open val eventType: String,
    open val aggregateId: String,
    open val version: Long = 1L
) {
    abstract fun getRoutingKey(): String
}

// 🏢 HQ 생성 이벤트
data class HqCreatedEvent(
    override val aggregateId: String,
    override val version: Long,
    val hqId: Long,
    val name: String,
    val representative: String,
    val address: String,
    val email: String?,
    val phoneNumber: String?
) : DomainEvent(
    eventType = "HqCreated",
    aggregateId = aggregateId,
    version = version
) {
    override fun getRoutingKey(): String = "hq.created"
    
    companion object {
        fun from(hq: Hq): HqCreatedEvent {
            return HqCreatedEvent(
                aggregateId = "hq-${hq.id?.value}",
                version = hq.version,
                hqId = hq.id?.value ?: 0L,
                name = hq.name,
                representative = hq.representative,
                address = hq.address.fullAddress,
                email = hq.email?.value,
                phoneNumber = hq.phoneNumber?.value
            )
        }
    }
}
```

### 2. 이벤트 발행자 구현
```kotlin
// 📡 Event Publisher
@Component
class HqEventPublisher(
    private val kafkaTemplate: KafkaTemplate<String, DomainEvent>
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    
    fun publishHqCreated(event: HqCreatedEvent) {
        try {
            kafkaTemplate.send(
                "organization-events",  // Topic
                event.getRoutingKey(),  // Key
                event                   // Value
            ).get() // 동기 전송으로 에러 확인
            
            logger.info("HqCreatedEvent published: {}", event.aggregateId)
        } catch (e: Exception) {
            logger.error("Failed to publish HqCreatedEvent: {}", event.aggregateId, e)
            throw EventPublishException("이벤트 발행 실패", e)
        }
    }
}
```

### 3. 이벤트 리스너 구현
```kotlin
// 👂 Event Listener (POS Server에서)
@Component
class OrganizationEventListener(
    private val hqSyncService: HqSyncService
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @KafkaListener(
        topics = ["organization-events"],
        groupId = "pos-server-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    fun handleHqCreated(
        @Payload event: HqCreatedEvent,
        @Header(KafkaHeaders.RECEIVED_KEY) key: String
    ) {
        try {
            logger.info("Received HqCreatedEvent: {}", event.aggregateId)
            
            // POS Server에서 HQ 정보 동기화
            hqSyncService.syncHq(event)
            
        } catch (e: Exception) {
            logger.error("Failed to handle HqCreatedEvent: {}", event.aggregateId, e)
            // 재시도 로직 또는 DLQ 처리
            throw e
        }
    }
}
```

### 4. 이벤트 카탈로그
```kotlin
// 📋 Event Catalog
object EventCatalog {
    
    // 조직 관련 이벤트
    object Organization {
        const val HQ_CREATED = "organization.hq.created"
        const val HQ_UPDATED = "organization.hq.updated"
        const val STORE_CREATED = "organization.store.created"
        const val POS_CREATED = "organization.pos.created"
    }
    
    // 상품 관련 이벤트
    object Product {
        const val PRODUCT_CREATED = "product.created"
        const val PRODUCT_UPDATED = "product.updated"
        const val PRICE_CHANGED = "product.price.changed"
        const val STOCK_UPDATED = "product.stock.updated"
    }
    
    // 판매 관련 이벤트
    object Sales {
        const val SALE_COMPLETED = "sales.completed"
        const val PAYMENT_PROCESSED = "sales.payment.processed"
        const val RECEIPT_GENERATED = "sales.receipt.generated"
    }
}
```

---

## 🏛️ Hexagonal Architecture 구현 패턴

### 1. Domain Entity 작성
```kotlin
// 💎 Rich Domain Model
class Hq private constructor(
    val id: HqId?,
    val name: String,
    val representative: String,
    val address: Address,
    val email: Email?,
    val phoneNumber: PhoneNumber?,
    val createdAt: Instant,
    val updatedAt: Instant,
    val version: Long
) {
    companion object {
        fun create(
            name: String,
            representative: String,
            address: Address,
            email: Email?,
            phoneNumber: PhoneNumber?
        ): Hq {
            validateBusinessRules(name, representative)
            
            return Hq(
                id = null,
                name = name,
                representative = representative,
                address = address,
                email = email,
                phoneNumber = phoneNumber,
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
                version = 1L
            )
        }
        
        private fun validateBusinessRules(name: String, representative: String) {
            require(name.isNotBlank()) { "본사명은 필수입니다" }
            require(representative.isNotBlank()) { "대표자명은 필수입니다" }
            require(name.length <= 100) { "본사명은 100자를 초과할 수 없습니다" }
        }
    }
    
    fun updateInfo(
        name: String,
        representative: String,
        address: Address,
        email: Email?,
        phoneNumber: PhoneNumber?
    ): Hq {
        validateBusinessRules(name, representative)
        
        return copy(
            name = name,
            representative = representative,
            address = address,
            email = email,
            phoneNumber = phoneNumber,
            updatedAt = Instant.now(),
            version = version + 1
        )
    }
    
    private fun copy(
        id: HqId? = this.id,
        name: String = this.name,
        representative: String = this.representative,
        address: Address = this.address,
        email: Email? = this.email,
        phoneNumber: PhoneNumber? = this.phoneNumber,
        createdAt: Instant = this.createdAt,
        updatedAt: Instant = this.updatedAt,
        version: Long = this.version
    ) = Hq(id, name, representative, address, email, phoneNumber, createdAt, updatedAt, version)
}
```

### 2. Value Objects 정의
```kotlin
// 💰 Money Value Object
@JvmInline
value class Money(val amount: BigDecimal) {
    init {
        require(amount >= BigDecimal.ZERO) { "금액은 0 이상이어야 합니다" }
        require(amount.scale() <= 2) { "소수점 2자리까지만 허용됩니다" }
    }
    
    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun minus(other: Money) = Money(amount - other.amount)
    operator fun times(multiplier: Int) = Money(amount * BigDecimal.valueOf(multiplier.toLong()))
    
    fun isGreaterThan(other: Money) = amount > other.amount
    fun isLessThan(other: Money) = amount < other.amount
    
    companion object {
        val ZERO = Money(BigDecimal.ZERO)
        fun of(amount: Double) = Money(BigDecimal.valueOf(amount))
        fun of(amount: String) = Money(BigDecimal(amount))
    }
}

// 📧 Email Value Object
@JvmInline
value class Email(val value: String) {
    init {
        require(isValidEmail(value)) { "올바른 이메일 형식이 아닙니다: $value" }
    }
    
    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$"
        return email.matches(emailPattern.toRegex())
    }
}

// 📱 PhoneNumber Value Object
@JvmInline
value class PhoneNumber(val value: String) {
    init {
        require(isValidPhoneNumber(value)) { "올바른 전화번호 형식이 아닙니다: $value" }
    }
    
    private fun isValidPhoneNumber(phone: String): Boolean {
        // 한국 전화번호 패턴 (010-0000-0000, 02-000-0000 등)
        val phonePattern = "^(01[016789]|02|0[3-9][0-9])-?[0-9]{3,4}-?[0-9]{4}$"
        return phone.replace("-", "").matches(phonePattern.toRegex())
    }
    
    fun formatted(): String {
        val numbers = value.replace("-", "")
        return when {
            numbers.startsWith("010") -> "${numbers.substring(0,3)}-${numbers.substring(3,7)}-${numbers.substring(7)}"
            numbers.startsWith("02") -> "${numbers.substring(0,2)}-${numbers.substring(2,5)}-${numbers.substring(5)}"
            else -> numbers
        }
    }
}
```

### 3. Repository 포트 정의
```kotlin
// 📤 Outbound Port (Repository Interface)
interface HqCommandRepository {
    fun save(hq: Hq): Hq
    fun delete(hqId: HqId)
    fun existsByName(name: String): Boolean
}

interface HqQueryRepository {
    fun findById(hqId: HqId): Hq?
    fun findByName(name: String): Hq?
    fun findAll(pageable: Pageable): Page<Hq>
    fun existsByName(name: String): Boolean
}

// 📥 Inbound Port (Use Case Interface)
interface CreateHqUseCase {
    fun handle(command: CreateHqCommand): CreateHqResult
}

interface GetHqUseCase {
    fun handle(query: GetHqByIdQuery): HqResult?
    fun handle(query: GetAllHqQuery): Page<HqResult>
}
```

### 4. Adapter 구현
```kotlin
// 🔌 Persistence Adapter
@Repository
class HqCommandRepositoryAdapter(
    private val hqJpaRepository: HqJpaRepository,
    private val hqPersistenceMapper: HqPersistenceMapper
) : HqCommandRepository {
    
    override fun save(hq: Hq): Hq {
        val jpaEntity = hqPersistenceMapper.toJpaEntity(hq)
        val savedEntity = hqJpaRepository.save(jpaEntity)
        return hqPersistenceMapper.toDomainEntity(savedEntity)
    }
    
    override fun delete(hqId: HqId) {
        hqJpaRepository.deleteById(hqId.value)
    }
    
    override fun existsByName(name: String): Boolean {
        return hqJpaRepository.existsByNameAndIsDeletedFalse(name)
    }
}

// 🌐 Web Adapter (Controller)
@RestController
@RequestMapping("/main/organization/hq")
class HqWebAdapter(
    private val createHqUseCase: CreateHqUseCase,
    private val getHqUseCase: GetHqUseCase,
    private val hqWebMapper: HqWebMapper
) {
    
    @PostMapping
    fun createHq(@RequestBody @Valid request: CreateHqRequest): ResponseEntity<CreateHqResponse> {
        val command = hqWebMapper.toCommand(request)
        val result = createHqUseCase.handle(command)
        val response = hqWebMapper.toResponse(result)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @GetMapping("/{hqId}")
    fun getHq(@PathVariable hqId: Long): ResponseEntity<HqResponse> {
        val query = GetHqByIdQuery(hqId)
        val result = getHqUseCase.handle(query)
            ?: return ResponseEntity.notFound().build()
        
        val response = hqWebMapper.toResponse(result)
        return ResponseEntity.ok(response)
    }
}
```

---

## 🧪 테스트 전략

### 1. Unit Tests (도메인 테스트)
```kotlin
// 🧪 Domain Entity Test
class HqTest {
    
    @Test
    fun `HQ 생성 시 유효한 정보로 생성되어야 한다`() {
        // given
        val name = "테스트 본사"
        val representative = "홍길동"
        val address = Address("서울시 강남구")
        val email = Email("test@example.com")
        val phoneNumber = PhoneNumber("010-1234-5678")
        
        // when
        val hq = Hq.create(name, representative, address, email, phoneNumber)
        
        // then
        assertThat(hq.name).isEqualTo(name)
        assertThat(hq.representative).isEqualTo(representative)
        assertThat(hq.address).isEqualTo(address)
        assertThat(hq.email).isEqualTo(email)
        assertThat(hq.phoneNumber).isEqualTo(phoneNumber)
        assertThat(hq.version).isEqualTo(1L)
    }
    
    @Test
    fun `HQ 생성 시 본사명이 빈 값이면 예외가 발생한다`() {
        // given
        val name = ""
        val representative = "홍길동"
        val address = Address("서울시 강남구")
        
        // when & then
        assertThatThrownBy {
            Hq.create(name, representative, address, null, null)
        }.isInstanceOf(IllegalArgumentException::class.java)
         .hasMessage("본사명은 필수입니다")
    }
}

// 💰 Value Object Test
class MoneyTest {
    
    @Test
    fun `Money 객체는 음수 금액을 허용하지 않는다`() {
        // when & then
        assertThatThrownBy {
            Money(BigDecimal("-100"))
        }.isInstanceOf(IllegalArgumentException::class.java)
         .hasMessage("금액은 0 이상이어야 합니다")
    }
    
    @Test
    fun `Money 객체는 덧셈이 정상적으로 작동한다`() {
        // given
        val money1 = Money.of("100.50")
        val money2 = Money.of("50.30")
        
        // when
        val result = money1 + money2
        
        // then
        assertThat(result.amount).isEqualByComparingTo(BigDecimal("150.80"))
    }
}
```

### 2. Integration Tests (어댑터 테스트)
```kotlin
// 🔗 Repository Integration Test
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(locations = ["classpath:application-test.yml"])
class HqCommandRepositoryAdapterTest {
    
    @Autowired
    private lateinit var hqJpaRepository: HqJpaRepository
    
    @Autowired
    private lateinit var testEntityManager: TestEntityManager
    
    private lateinit var hqCommandRepository: HqCommandRepositoryAdapter
    private lateinit var hqPersistenceMapper: HqPersistenceMapper
    
    @BeforeEach
    fun setUp() {
        hqPersistenceMapper = HqPersistenceMapperImpl()
        hqCommandRepository = HqCommandRepositoryAdapter(hqJpaRepository, hqPersistenceMapper)
    }
    
    @Test
    fun `HQ 저장이 정상적으로 작동한다`() {
        // given
        val hq = Hq.create(
            name = "테스트 본사",
            representative = "홍길동",
            address = Address("서울시 강남구"),
            email = Email("test@example.com"),
            phoneNumber = PhoneNumber("010-1234-5678")
        )
        
        // when
        val savedHq = hqCommandRepository.save(hq)
        
        // then
        assertThat(savedHq.id).isNotNull
        assertThat(savedHq.name).isEqualTo("테스트 본사")
        
        // DB 확인
        val foundEntity = testEntityManager.find(HqJpaEntity::class.java, savedHq.id?.value)
        assertThat(foundEntity).isNotNull
        assertThat(foundEntity.name).isEqualTo("테스트 본사")
    }
}
```

### 3. End-to-End Tests
```kotlin
// 🔄 E2E Test
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@TestMethodOrder(OrderAnnotation::class)
class HqE2ETest {
    
    @Autowired
    private lateinit var restTemplate: TestRestTemplate
    
    @Container
    companion object {
        @JvmStatic
        val mysqlContainer = MySQLContainer<Nothing>("mysql:8.0").apply {
            withDatabaseName("test_db")
            withUsername("test")
            withPassword("test")
            withInitScript("schema.sql")
        }
    }
    
    @Test
    @Order(1)
    fun `HQ를 생성하고 조회할 수 있다`() {
        // given
        val createRequest = CreateHqRequest(
            name = "E2E 테스트 본사",
            representative = "김테스트",
            address = "서울시 테스트구",
            email = "e2e@test.com",
            phoneNumber = "010-9999-8888"
        )
        
        // when - 생성
        val createResponse = restTemplate.postForEntity(
            "/main/organization/hq",
            createRequest,
            CreateHqResponse::class.java
        )
        
        // then - 생성 확인
        assertThat(createResponse.statusCode).isEqualTo(HttpStatus.CREATED)
        assertThat(createResponse.body?.hqId).isNotNull
        
        val hqId = createResponse.body!!.hqId
        
        // when - 조회
        val getResponse = restTemplate.getForEntity(
            "/main/organization/hq/$hqId",
            HqResponse::class.java
        )
        
        // then - 조회 확인
        assertThat(getResponse.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(getResponse.body?.name).isEqualTo("E2E 테스트 본사")
    }
}
```

---

## 📊 모니터링 및 로깅

### 1. 구조화된 로깅
```kotlin
// 📝 Structured Logging
@Component
class HqCommandHandler(
    private val hqCommandRepository: HqCommandRepository,
    private val hqEventPublisher: HqEventPublisher
) : CreateHqUseCase {
    
    companion object {
        private val logger = LoggerFactory.getLogger(HqCommandHandler::class.java)
    }
    
    override fun handle(command: CreateHqCommand): CreateHqResult {
        val traceId = MDC.get("traceId") ?: UUID.randomUUID().toString()
        
        logger.info("Creating HQ started", 
            kv("traceId", traceId),
            kv("command", "CreateHq"),
            kv("hqName", command.name)
        )
        
        try {
            val hq = Hq.create(command.name, command.representative, ...)
            val savedHq = hqCommandRepository.save(hq)
            
            logger.info("HQ created successfully",
                kv("traceId", traceId),
                kv("hqId", savedHq.id?.value),
                kv("hqName", savedHq.name)
            )
            
            hqEventPublisher.publishHqCreated(HqCreatedEvent.from(savedHq))
            
            return CreateHqResult.from(savedHq)
            
        } catch (e: Exception) {
            logger.error("Failed to create HQ",
                kv("traceId", traceId),
                kv("command", "CreateHq"),
                kv("hqName", command.name),
                kv("error", e.message)
            )
            throw e
        }
    }
}
```

### 2. 메트릭 수집
```kotlin
// 📈 Metrics Collection
@Component
class MetricsCollector {
    
    private val hqCreationCounter = Counter.builder("hq.creation.total")
        .description("Total HQ creation attempts")
        .register(Metrics.globalRegistry)
    
    private val hqCreationTimer = Timer.builder("hq.creation.duration")
        .description("HQ creation duration")
        .register(Metrics.globalRegistry)
    
    fun incrementHqCreation() {
        hqCreationCounter.increment()
    }
    
    fun recordHqCreationTime(duration: Duration) {
        hqCreationTimer.record(duration)
    }
}

// 사용 예시
@Component
class HqCommandHandler(
    private val hqCommandRepository: HqCommandRepository,
    private val metricsCollector: MetricsCollector
) : CreateHqUseCase {
    
    override fun handle(command: CreateHqCommand): CreateHqResult {
        val startTime = Instant.now()
        
        try {
            metricsCollector.incrementHqCreation()
            
            val hq = Hq.create(...)
            val savedHq = hqCommandRepository.save(hq)
            
            return CreateHqResult.from(savedHq)
            
        } finally {
            val duration = Duration.between(startTime, Instant.now())
            metricsCollector.recordHqCreationTime(duration)
        }
    }
}
```

---

## 🚀 배포 가이드

### 1. Docker를 이용한 로컬 배포
```bash
# 전체 시스템 빌드 및 실행
./gradlew buildAllServices
docker-compose up -d

# 개별 서비스 재배포
docker-compose restart main-server
docker-compose restart pos-server
```

### 2. 환경별 설정 관리
```yaml
# application-local.yml (로컬 개발)
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/main_server
  
logging:
  level:
    com.gijun: DEBUG

# application-docker.yml (Docker 환경)
spring:
  datasource:
    url: jdbc:mysql://mysql-main:3306/main_server
  
logging:
  level:
    com.gijun: INFO

# application-prod.yml (운영 환경)
spring:
  datasource:
    url: ${DB_URL}
  
logging:
  level:
    com.gijun: WARN
```

---

## 💡 Best Practices

### 1. 코드 품질
- **정적 분석 도구**: ktlint, detekt 사용
- **테스트 커버리지**: 최소 80% 이상 유지
- **의존성 주입**: Constructor Injection 선호
- **불변 객체**: 가능한 한 immutable 객체 사용

### 2. 성능 최적화
- **지연 로딩**: JPA Entity에서 적절한 FetchType 설정
- **캐싱**: 자주 조회되는 데이터는 Redis 캐싱 적용
- **배치 처리**: 대량 데이터 처리 시 배치 사이즈 최적화
- **인덱스**: 검색 성능을 위한 적절한 DB 인덱스 설정

### 3. 보안
- **입력 검증**: 모든 입력값에 대한 검증 로직
- **SQL Injection 방지**: JPA Query 사용 시 파라미터 바인딩
- **민감 정보 보호**: 로그에서 개인정보 마스킹
- **API 인증**: JWT 기반 인증/인가 (향후 추가 예정)

---

*이 가이드는 지속적으로 업데이트되며, 모든 개발자가 일관된 방식으로 개발할 수 있도록 돕는 것이 목적입니다.*

*Last updated: 2025-01-14*