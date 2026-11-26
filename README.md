# ProyectoDeGrado

## Catálogo externo ReKids Sandbox

Las rutas `GET /api/v1/products/public/rekids/branches`,
`GET /api/v1/products/public/rekids/branches/{branchId}/products`,
`GET /api/v1/products/public/rekids/by-branches` y
`GET /api/v1/products/public/rekids/products/{productId}` consultan
`https://testendpoints.onrender.com` mediante Spring Cloud OpenFeign. Estas rutas no
leen ni completan el catálogo desde la tabla local `products`; si el gateway no está
disponible, la petición falla en vez de devolver datos locales o de muestra.

Configurar `REKIDS_API_TOKEN` como variable privada del servicio Java en Render con
la credencial `rk_test_...` del sandbox. No ponerla en Angular ni en el repositorio.
El servicio externo también debe estar habilitado: su `/health` debe informar
`"configured": true`, y el SQL de credencial de pruebas debe haberse aplicado a la
base sintética. Un `503 INTEGRATION_NOT_ENABLED` indica que ese servicio externo aún
no está habilitado; no se sustituye por una consulta a la base del backend.