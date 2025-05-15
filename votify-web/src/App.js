import React from 'react';
import { useTranslation } from 'react-i18next';

function App() {
  const { t } = useTranslation();

  return (
    <div>
      <h1>{t('Welcome to Votify')}</h1>
      <button>{t('Create new poll')}</button>
    </div>
  );
}

export default App;
