UPDATE "mail_content" SET "subject"='[(#{subject(${requestOwner.firstName}, ${requestOwner.lastName},${subject})})]',"body"='<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
   <head data-th-replace="layout :: header"></head>
   <body>
      <div
         th:replace="layout :: email_base(upperMainContentArea = ~{::#main-content},bottomSecondaryContentArea = ~{::#secondary-content})">
         <!--/*  Upper main-content */-->
         <section id="main-content">
            <!--/* If the sender has added a customized message */-->
            <th:block data-th-if="${!#strings.isEmpty(body)}">
               <div th:replace="layout :: contentMessageSection( ~{::#message-title}, ~{::#message-content})">
                  <span id="message-title">
                  <span data-th-text="#{msgFrom}">You have a message from</span>
                  <b data-th-text="#{name(${requestOwner.firstName} , ${requestOwner.lastName})}">Peter Wilson</b> :
                  </span>
                  <span id="message-content" data-th-text="${body}" style="white-space: pre-line;">
                  Hi Amy,<br>
                  As agreed,  could you send me the report. Feel free to contact me if need be. <br/>Best regards, Peter.
                  </span>
               </div>
            </th:block>
            <!--/* End of customized message */-->
            <!--/* main-content container */-->
            <div th:replace="layout :: contentUpperSection(~{::#section-content})">
               <div id="section-content">
                  <!--/* Greetings for external or internal user */-->
                  <div>
                     <th:block data-th-replace="layout :: greetings(${requestRecipient.mail})"/>
                  </div>
                  <!--/* End of Greetings for external or internal recipient */-->
                  <!--/* Main email  message content*/-->
                  <p>
                   <th:block data-th-if="(${!request.wasPreviouslyCreated})">
                       <span data-th-utext="#{mainMsg(${requestOwner.firstName},${requestOwner.lastName},${subject})}">
                          Peter Wilson invited  you to upload  some files in the Upload Request depot labeled : subject.
                       </span>
                   </th:block>
                    <th:block data-th-if="(${request.wasPreviouslyCreated})">
                       <span data-th-text="#{msgAlt(${requestOwner.firstName} , ${requestOwner.lastName})}"> Peter Wilson''s Upload Request depot is now activated..</span>
                     </th:block>
                     <br/>
                     <!--/* Check if the external user has a password protected file share */-->
                     <span data-th-if="(${!protected})">
                     <span data-th-text="#{msgUnProtected}">In order to access it click the link below.</span>
                     </span>
                     <span data-th-if="(${protected})">
                     <span data-th-text="#{msgProtected}">In order to access it click the link below and enter the provided password.</span>
                     </span>
                  </p>
                  <th:block data-th-replace="layout :: actionButtonLink(#{buttonMsg},${requestUrl})"/>
                  <!--/* End of Main email message content*/-->
               </div>
               <!--/* End of section-content*/-->
            </div>
            <!--/* End of main-content container */-->
         </section>
         <!--/* End of upper main-content*/-->
         <!--/* Secondary content for  bottom email section */-->
         <section id="secondary-content">
            <div data-th-if="(${protected})">
               <th:block data-th-replace="layout :: infoStandardArea(#{password},${password})"/>
            </div>
            <div data-th-if="${!#strings.isEmpty(request.expirationDate)}">
               <th:block data-th-replace="layout :: infoDateArea(#{closureDate},${request.expirationDate})"/>
            </div>
           <div data-th-if="(${totalMaxDepotSize})">
                    <th:block data-th-replace="layout :: infoStandardArea(#{depotSize},${totalMaxDepotSize})"/>
            </div>
            <div data-th-if="!(${isRestricted})">
               <th:block data-th-replace="layout :: infoRecipientListingArea(#{recipientsOfDepot},${recipients})"/>
            </div>
         </section>
         <!--/* End of Secondary content for bottom email section */-->
      </div>
   </body>
</html>',"messages_french"='buttonMsg = Accès
closureDate = Date de clôture
depotSize = Taille
mainMsg = <b>{0} {1}</b> vous invite à déposer des fichiers dans le dépôt : <b>{2}</b>.
msgAlt = L''''invitation de {0} {1} est désormais active.
msgFrom = Le message de
msgProtected = Vous pouvez déverrouiller le dépôt en suivant le lien ci-dessous et en saisissant le mot de passe fourni.
msgUnProtected = Vous pouvez y accéder en suivant le lien ci-dessous.
name = {0} {1}
password = Mot de passe
recipientsOfDepot = Destinataires
subject = {0} {1} vous invite à déposer des fichiers dans le dépôt : {2}',"messages_english"='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> invited you to its upload request : <b>{2}</b>.
msgFrom = Message from
msgAlt = The repository from {0} {1} is now active.
msgProtected = Unlock it by following the link below and entering the password.
msgUnProtected = Access it by following the link below.
name = {0} {1}
password = Password
recipientsOfDepot = Recipients
subject = {0} {1} invited you to its upload request : {2}',"messages_russian"='buttonMsg = Access
closureDate = Closure date
depotSize = Allowed size
mainMsg = <b>{0} {1}</b> invited you to its upload request : <b>{2}</b>.
msgFrom = Message from
msgAlt = The repository from {0} {1} is now active.
msgProtected = Unlock it by following the link below and entering the password.
msgUnProtected = Access it by following the link below.
name = {0} {1}
password = Password
recipientsOfDepot = Recipients
subject = {0} {1} invited you to its upload request : {2}' WHERE "id"=16;