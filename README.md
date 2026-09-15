# fiap-soat-vehicle-resale-ap

📂 seu-projeto-backend
├── 📂 core                  # O Hexágono (Isolado de frameworks/tecnologias)
│   ├── 📂 domain            # Modelos de domínio e regras de negócio puras
│   │   ├── 📂 model         # Entidades, Objetos de Valor (Value Objects)
│   │   └── 📂 exception     # Exceções de negócio (ex: SaldoInsuficienteException)
│   │
│   └── 📂 application       # Casos de uso e as Portas de entrada/saída
│       ├── 📂 ports         # As interfaces de comunicação
│       │   ├── 📂 inbound   # Portas de Entrada (Driving Ports / Use Cases)
│       │   └── 📂 outbound  # Portas de Saída (Driven Ports / SPIs)
│       └── 📂 usecase       # Implementação dos casos de uso (Lógica da aplicação)
│
└── 📂 infrastructure        # O lado de fora do Hexágono (Adapters e Configurações)
├── 📂 adapters          # Implementações tecnológicas das portas
│   ├── 📂 inbound       # Adaptadores de Entrada (REST controllers, CLI, Filas)
│   └── 📂 outbound      # Adaptadores de Saída (Bancos de dados, APIs externas)
│
└── 📂 configuration     # Frameworks, Injeção de Dependência e inicialização

