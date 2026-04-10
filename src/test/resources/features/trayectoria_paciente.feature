Feature: Trayectoria Clínica del Paciente en RLApp-V2
  Como administrador del sistema (Supervisor)
  Quiero consultar la trayectoria persistida de un paciente después de su ciclo completo de atención
  Para garantizar trazabilidad, continuidad y visibilidad del recorrido clínico

  Scenario: Consultar trayectoria después del ciclo completo de atención
    Given que el personal médico se autentica en el sistema
    When se registra la llegada de un paciente a recepción
    And el operador de caja llama al paciente para validar el pago
    And el operador de caja valida el pago del paciente
    And el administrador médica activa una de las salas
    And el doctor llama al siguiente paciente en la sala
    And el doctor inicia la consulta médica
    And el doctor finaliza la consulta médica exitosamente
    And se espera la propagación de eventos de trayectoria
    Then el sistema debe retornar al menos una trayectoria para el paciente atendido
    And la trayectoria debe contener las etapas del recorrido clínico completo
    And la trayectoria debe estar en estado finalizado

  Scenario: Consultar trayectoria por identificador directo
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
    Then el detalle de la trayectoria debe incluir las etapas registradas
    And cada etapa debe contener timestamp y evento fuente

  Scenario: Buscar trayectoria de paciente inexistente retorna vacío
    Given que el personal médico se autentica en el sistema
    When se busca la trayectoria de un paciente que no existe en el sistema
    Then el sistema debe retornar cero trayectorias encontradas

  Scenario: Reconstruir trayectorias en modo simulación
    Given que el personal médico se autentica en el sistema
    When se registra la llegada de un paciente a recepción
    And se espera la propagación de eventos de trayectoria
    And se solicita la reconstrucción de trayectorias en modo simulación
    Then el resultado de la reconstrucción debe indicar modo simulación
    And el resultado debe reportar eventos procesados

  Scenario: Consultar discovery sin autenticación retorna 401
    When se consulta el discovery de trayectorias sin autenticación
    Then el sistema debe rechazar la solicitud con código 401

  Scenario: Consultar detalle sin autenticación retorna 401
    When se consulta el detalle de una trayectoria sin autenticación
    Then el sistema debe rechazar la solicitud con código 401

  Scenario: Reconstruir trayectorias sin autenticación retorna 401
    When se intenta reconstruir una trayectoria sin autenticación
    Then el sistema debe rechazar la solicitud con código 401

  Scenario: Reconstruir trayectorias con supervisor retorna 403
    Given que el personal médico se autentica en el sistema
    When un supervisor intenta reconstruir trayectorias sin privilegios de soporte
    Then el sistema debe rechazar la solicitud con código 403
