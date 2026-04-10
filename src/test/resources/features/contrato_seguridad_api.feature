Feature: Contrato y seguridad del API de trayectorias clínicas
  Como QA de integración
  Quiero validar los contratos HTTP, esquemas de respuesta y restricciones de seguridad
  Para garantizar que el API cumple su especificación y protege recursos sensibles (Contract + Negative + Security Testing)

  # --- CONTRACT TESTING: Estructura de respuesta ---

  Scenario: Contrato del discovery — estructura de respuesta válida
    Given que el personal médico se autentica en el sistema
    When se registra la llegada de un paciente a recepción
    And el operador de caja llama al paciente para validar el pago
    And el operador de caja valida el pago del paciente
    And el administrador médica activa una de las salas
    And el doctor llama al siguiente paciente en la sala
    And el doctor inicia la consulta médica
    And el doctor finaliza la consulta médica exitosamente
    And se espera la propagación de eventos de trayectoria
    Then la respuesta del discovery debe cumplir el contrato esperado

  Scenario: Contrato del detalle por ID — estructura de respuesta válida
    Given que el personal médico se autentica en el sistema
    When se registra la llegada de un paciente a recepción
    And el operador de caja llama al paciente para validar el pago
    And el operador de caja valida el pago del paciente
    And el administrador médica activa una de las salas
    And el doctor llama al siguiente paciente en la sala
    And el doctor inicia la consulta médica
    And el doctor finaliza la consulta médica exitosamente
    And se espera la propagación de eventos de trayectoria
    And se descubre la trayectoria del paciente por su identificador
    Then la respuesta del detalle debe contener campos obligatorios del contrato

  # --- NEGATIVE TESTING: Inputs inválidos ---

  Scenario: Discovery con patientId vacío retorna error
    Given que el personal médico se autentica en el sistema
    When se consulta el discovery con patientId vacío
    Then el sistema debe responder con código 400

  Scenario: Detalle con trajectoryId malformado retorna error
    Given que el personal médico se autentica en el sistema
    When se consulta el detalle con trajectoryId malformado "NOT-A-VALID-UUID-!!!"
    Then el sistema debe responder con código de error del contrato

  # --- SECURITY: RBAC y autenticación ---

  Scenario: Rebuild con token expirado o inválido retorna 401
    When se intenta reconstruir trayectorias con token inválido "Bearer INVALID.TOKEN.VALUE"
    Then el sistema debe responder con código 401

  Scenario Outline: Endpoints protegidos rechazan peticiones anónimas
    When se llama al endpoint "<endpoint>" sin autenticación con método "<metodo>"
    Then el sistema debe responder con código 401

    Examples:
      | endpoint                          | metodo |
      | /api/patient-trajectories         | GET    |
      | /api/patient-trajectories/rebuild | POST   |
