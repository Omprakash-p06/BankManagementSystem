const fs = require("fs");
const path = require("path");

const baseUrl = process.env.BASE_URL;
if (!baseUrl) {
  console.error("BASE_URL env var is required for Netlify builds.");
  console.error("Example: https://<your-backend-domain>/BankManagementSystem");
  process.exit(1);
}

const configPath = path.join(__dirname, "..", "src", "main", "webapp", "config.js");
const config = {
  BASE_URL: baseUrl
};
const content = `window.APP_CONFIG = ${JSON.stringify(config, null, 2)};\n`;

fs.writeFileSync(configPath, content, "utf8");
console.log(`Wrote ${configPath}`);
