Feature: Validación de datos límite y particiones de equivalencia en registro de paciente
  Como QA automatizador
  Quiero validar el comportamiento del API con datos en los límites y particiones inválidas
  Para garantizar robustez y cobertura basada en riesgo (DDT + Boundary Value + Equivalence Partitioning)

  Background:
    Given que el personal médico se autentica en el sistema

  # DDT: API actualmente acepta estos datos sin validación server-side.
  # Se documenta el comportamiento real para evidencia de cobertura.
  Scenario Outline: Registro con datos límite aceptados por el API
    When se intenta registrar un paciente con nombre "<nombre>" y prioridad "<prioridad>" y notas "<notas>"
    Then el sistema debe responder con código <codigoEsperado>

    Examples: Valores vacíos — partición sin validación actual
      | nombre | prioridad | notas              | codigoEsperado |
      |        | 1         | Checkup normal     | 200            |

    Examples: Valores en el límite superior — boundary analysis
      | nombre                                                                                                                                                                                                                                                   | prioridad | notas              | codigoEsperado |
      | AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA | 1         | Boundary test      | 200            |

    Examples: Prioridad fuera de rango — equivalence partitioning
      | nombre         | prioridad | notas              | codigoEsperado |
      | John Boundary  | -1        | Prioridad negativa | 200            |
      | John Boundary  | 999       | Prioridad excesiva | 200            |

  Scenario: Registro duplicado con mismo queueId y patientId
    When se registra un paciente con datos válidos generados
    And se intenta registrar el mismo paciente nuevamente con los mismos identificadores
    Then el sistema debe rechazar el duplicado con código 409 o 400

  Scenario: Registro sin token de autenticación retorna 401
    When se intenta registrar un paciente sin autenticación
    Then el sistema debe responder con código 401
