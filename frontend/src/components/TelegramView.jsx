import React, { useState, useEffect } from 'react';
import { Send, KeyRound, Copy, Check, Clock, ExternalLink, ShieldCheck, Terminal } from 'lucide-react';
import { api } from '../api';

export default function TelegramView() {
  const [codeData, setCodeData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [copied, setCopied] = useState(false);
  const [timeLeft, setTimeLeft] = useState(0);

  useEffect(() => {
    let timer;
    if (codeData && codeData.expiresAt) {
      const updateTimer = () => {
        const diff = Math.max(0, Math.floor((new Date(codeData.expiresAt).getTime() - new Date().getTime()) / 1000));
        setTimeLeft(diff);
      };

      updateTimer();
      timer = setInterval(updateTimer, 1000);
    }
    return () => clearInterval(timer);
  }, [codeData]);

  const generateCode = async () => {
    setError('');
    setLoading(true);
    try {
      const data = await api.generateTelegramLinkCode();
      setCodeData(data);
      setCopied(false);
    } catch (err) {
      setError(err.message || 'Failed to generate link code');
    } finally {
      setLoading(false);
    }
  };

  const handleCopy = (text) => {
    navigator.clipboard.writeText(text);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  const formatTimer = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  return (
    <div className="space-y-8 max-w-4xl mx-auto">
      {/* Header */}
      <div>
        <h1 className="text-3xl font-black uppercase tracking-tight text-black flex items-center gap-2">
          <Send className="w-8 h-8 text-black" /> Telegram Bot Synchronization
        </h1>
        <p className="text-sm font-bold text-gray-700">Link your account to record expenses & income directly from Telegram</p>
      </div>

      {/* Main Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        
        {/* Left Column: OTP Code Generator */}
        <div className="nb-card-yellow space-y-6">
          <div className="flex items-center gap-3">
            <div className="bg-black text-white p-2 rounded">
              <KeyRound className="w-6 h-6" />
            </div>
            <div>
              <h3 className="text-xl font-black uppercase text-black">1-Time Link Code</h3>
              <p className="text-xs font-bold text-black">Expires in 10 minutes</p>
            </div>
          </div>

          {error && (
            <div className="bg-[#FF2A85] text-white border-2 border-black p-3 rounded font-bold text-xs">
              ⚠️ {error}
            </div>
          )}

          {!codeData ? (
            <div className="text-center py-6 space-y-4">
              <p className="text-xs font-bold text-black leading-relaxed">
                Click below to generate a unique authorization code to connect your Telegram account.
              </p>
              <button
                onClick={generateCode}
                disabled={loading}
                className="nb-btn bg-black text-white w-full py-3 justify-center text-sm"
              >
                {loading ? 'Generating Code...' : 'Generate 1-Time Link Code'}
              </button>
            </div>
          ) : (
            <div className="space-y-4">
              {/* Code Card */}
              <div className="border-3 border-black bg-white p-6 rounded-lg text-center space-y-2 nb-shadow">
                <span className="text-xs font-black uppercase text-gray-500 tracking-wider">Your Link Code</span>
                <div className="text-4xl font-black tracking-widest text-black py-2 bg-[#F4F0EA] border-2 border-black rounded">
                  {codeData.code}
                </div>
                
                <div className="flex items-center justify-center gap-2 text-xs font-bold text-gray-700 pt-1">
                  <Clock className="w-4 h-4 text-[#FF2A85]" />
                  <span>Time Remaining: <strong className="text-black font-black">{formatTimer(timeLeft)}</strong></span>
                </div>
              </div>

              {/* Action Buttons */}
              <div className="flex flex-col sm:flex-row gap-3">
                <button
                  onClick={() => handleCopy(`/link ${codeData.code}`)}
                  className="nb-btn bg-white flex-1 justify-center"
                >
                  {copied ? <Check className="w-4 h-4 text-green-600" /> : <Copy className="w-4 h-4" />}
                  {copied ? 'Copied Command!' : 'Copy Command'}
                </button>

                <button
                  onClick={generateCode}
                  className="nb-btn bg-black text-white justify-center"
                  title="Refresh Code"
                >
                  Refresh
                </button>
              </div>
            </div>
          )}
        </div>

        {/* Right Column: Instructions & Bot Commands */}
        <div className="nb-card bg-white space-y-6">
          <div className="flex items-center gap-2 border-b-2 border-black pb-3">
            <Terminal className="w-5 h-5" />
            <h3 className="text-lg font-black uppercase">How to Connect</h3>
          </div>

          <ol className="space-y-3 text-xs font-bold text-gray-800 list-decimal list-inside">
            <li className="leading-relaxed">Click <strong>Generate 1-Time Link Code</strong> on the left.</li>
            <li className="leading-relaxed">Copy the code (e.g. <code className="bg-[#FFE600] px-1 border border-black rounded">/link A3B7K9X2</code>).</li>
            <li className="leading-relaxed">Send the command to your Telegram Bot in chat.</li>
            <li className="leading-relaxed">Look for the confirmation: <span className="nb-badge nb-badge-green py-0.5">✅ Telegram Linked</span></li>
          </ol>

          <div className="border-2 border-black bg-[#F4F0EA] p-4 rounded space-y-3">
            <h4 className="text-xs font-black uppercase text-black flex items-center gap-1.5">
              <ShieldCheck className="w-4 h-4 text-black" /> Available Bot Commands
            </h4>
            <div className="text-xs font-bold space-y-1.5 text-black">
              <p className="flex items-center justify-between border-b border-black/20 pb-1">
                <code>/spent 450 Food Domino's</code>
                <span className="text-[10px] uppercase text-gray-600">Add Expense</span>
              </p>
              <p className="flex items-center justify-between border-b border-black/20 pb-1">
                <code>/income 50000 Salary</code>
                <span className="text-[10px] uppercase text-gray-600">Add Income</span>
              </p>
              <p className="flex items-center justify-between border-b border-black/20 pb-1">
                <code>/balance</code>
                <span className="text-[10px] uppercase text-gray-600">View Savings</span>
              </p>
              <p className="flex items-center justify-between border-b border-black/20 pb-1">
                <code>/budgets</code>
                <span className="text-[10px] uppercase text-gray-600">View Budgets</span>
              </p>
              <p className="flex items-center justify-between">
                <code>/setbudget 5000 Food monthly</code>
                <span className="text-[10px] uppercase text-gray-600">Set Budget</span>
              </p>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
