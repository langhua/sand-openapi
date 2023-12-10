/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_BASE_API: string;
  readonly VITE_APP_NAME: string;
  readonly VITE_SANDFLOWER_SERVER: string;
  readonly VITE_SANDFLOWER_OPENAPI_FILETREE: string;
  readonly VITE_SANDFLOWER_OPENAPI_FILEDETAILS: string;
  readonly VITE_OPENAPI_BASE_URL: string;
}
  
// eslint-disable-next-line no-unused-vars
interface ImportMeta {
  readonly env: ImportMetaEnv
}